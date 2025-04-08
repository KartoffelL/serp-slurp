package Kartoffel.Licht;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Config {
	
	private static List<Config> autoSave = new ArrayList<Config>();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			save(); //Autosave
		}));
	}
	/**
	 * Statically loads the config given the pathname. This config will be automatically saved when the JVM exits.
	 * @param name the pathname
	 * @param a if to return null when the config is already in use
	 * @return the Config
	 * @throws IOException
	 */
	public static Config load(String name, boolean a) throws IOException{
		File f = new File(name);
		var c = new Config(f);
		FileLock l = FileChannel.open(f.toPath(), StandardOpenOption.WRITE).tryLock();
		if(l == null && a)
			return null;
		c.rememberLock = l;
		autoSave.add(c);
		return c;
	}
	/**
	 * Saves all static Configs.
	 */
	public static void save() {
		for(Config c : autoSave)
			try {
				if(c.rememberLock != null) {
					if(c.rememberLock.isValid()) {
						c.rememberLock.release();
						c.rememberLock.channel().close();
					}
				}
				c.saveConfig(c.rememberMe);
			} catch (Throwable e) {
				System.err.println("Failed to autosave config: " + c.rememberMe + "!");
				e.printStackTrace();
			}
	}
	/**
	 * Saves the given Config and removes it from the autosave.
	 * @param c
	 * @throws IOException
	 */
	public static void remove(Config c) {
		if(c.rememberMe == null)
			throw new IllegalStateException("Config not suitable for saving like this!");
		try {
			if(c.rememberLock != null) {
				c.rememberLock.release();
				c.rememberLock.channel().close();
			}
			c.saveConfig(c.rememberMe);
		} catch (Throwable e) {
			System.err.println("Failed to autosave config: " + c.rememberMe + "!");
			e.printStackTrace();
		}
		autoSave.remove(c);
	}
	
	private HashMap<String, Object> config = new HashMap<String, Object>(); //Either number, string or list
	private File rememberMe = null;
	private FileLock rememberLock = null;
	
	final static private String MESSAGE = "#Generated Config File";
	final static private char LSIT_SEPERATOR = '/';
	
	public Config(File f) throws IOException {
		rememberMe = f;
		if(!f.exists()) {
			f.createNewFile();
			return;
		}
		FileInputStream fis = new FileInputStream(f);
		vConfig(fis);
		fis.close();
	}
	
	public Config(InputStream fis) {
		vConfig(fis);
	}
	
	public void vConfig(InputStream fis) {
			try (Scanner sc = new Scanner(fis)) {
				List<Object> l = new ArrayList<Object>(1); //Only one
				while(sc.hasNextLine()) {
					String line = sc.nextLine();
					if(line.startsWith("#")) //Ignore
						continue;
					String[] a = line.split(":", 2);
					if(a.length < 2) //Ignore
						return;
					String property = a[0].trim();
					String content = a[1].trim();
					readPrimitive(l, property, content);
					if(l.size() > 0)
					config.put(property, l.get(0));
					l.clear();
				}
			}
	}
	private void readPrimitive(List<Object> res, String property, String content) {
		if(content.isBlank()) //Nothing to see here
			return;
		if(content.startsWith("\"")) { //String
			try {
				res.add(URLDecoder.decode(content.substring(1, content.length()-1), StandardCharsets.US_ASCII));
			} catch (Exception e) {
				System.err.println("Invalid property '"+property+"'! '"+content+"' isin't a parseable string!");
				e.printStackTrace();
			}
		} else if(content.startsWith("[")) {
			String line2 = content.substring(1, content.length()-1); //Remove brackets
			String[] vs = line2.split(""+LSIT_SEPERATOR);
			List<Object> l = new ArrayList<Object>();
			for(String v : vs)
				readPrimitive(l, property, v);
			res.add(l);
		} else { //Should be Number
			try {
				double d = Double.parseDouble(content);
				res.add(d);
			} catch (NumberFormatException e) {
				System.err.println("Invalid property '"+property+"'! '"+content+"' isin't a parseable double!");
				e.printStackTrace();
			}
		}
	}
	/**
	 * Creates a new empty Config
	 */
	public Config() {
		
	}

	public void saveConfig(File f) throws IOException {
			FileOutputStream fos = new FileOutputStream(f);
			saveConfig(fos);
			fos.close();
	}
	public OutputStream saveConfig(OutputStream fos) {
		PrintWriter pw = new PrintWriter(fos);
		pw.println(MESSAGE);
		config.forEach((a, b)->{
			if(b == null)
				return;
			pw.print(a.trim());
			pw.print(':');
			pw.print(" ");
			writePrimitive(pw, a, b);
			pw.println();
		});
		pw.flush();
		return fos;
	}
	private void writePrimitive(PrintWriter pw, String a, Object b) {
		if(b == null)
			return;
		if(b instanceof String) {
			pw.print('"');
			pw.print(URLEncoder.encode((String) b, StandardCharsets.US_ASCII));
			pw.print('"');
			return;
		}
		if(b instanceof Number) {
			Number n = (Number) b;
			pw.print(n.doubleValue());
			return;
		}
		if(b instanceof Collection<?>) {
			pw.print('[');
			for(Object ab : (Collection<?>)b) {
				writePrimitive(pw, a, ab);
				pw.print(LSIT_SEPERATOR);
			}
			pw.print(']');
			return;
		}
		System.err.println("Can't save Property '"+a+"' as " + b);
	}
	
	public Config defaultNumber(String property, double def) {
		check(property);
		this.config.putIfAbsent(property, def);
		return this;
	}
	public Config defaultString(String property, CharSequence def) {
		check(property);
		this.config.putIfAbsent(property, def);
		return this;
	}
	public Config defaultList(String property, Collection<?> def) {
		check(property);
		this.config.putIfAbsent(property, def);
		return this;
	}
	
	public void setNumber(String property, double value) {
		check(property);
		this.config.put(property, value);
	}
	public void setString(String property, CharSequence value) {
		check(property);
		this.config.put(property, value);
	}
	public void setList(String property, Collection<?> value) {
		check(property);
		this.config.put(property, value);
	}
	private void check(String p) {
		if(p.contains(":"))
			throw new RuntimeException("Property Name cannot contain ':'! Property: '"+p+"'");
		if(p.startsWith("#"))
			throw new RuntimeException("Property Name may not start with '#'! Property: '"+p+"'");
	}
	
	public void ensureListNoDuplicates(String property) {
		List<?> l = getList(property);
		setList(property, l.stream().distinct().collect(Collectors.toList()));
	}
	
	public Object getProperty(String property) {
		return this.config.get(property);
	}
	public double getDouble(String property) {
		return ((Number)this.config.get(property)).doubleValue();
	}
	public float getFloat(String property) {
		return ((Number)this.config.get(property)).floatValue();
	}
	public int getInt(String property) {
		return ((Number)this.config.get(property)).intValue();
	}
	public long getLong(String property) {
		return ((Number)this.config.get(property)).longValue();
	}
	public short getShort(String property) {
		return ((Number)this.config.get(property)).shortValue();
	}
	public byte getByte(String property) {
		return ((Number)this.config.get(property)).byteValue();
	}
	public ArrayList<?> getList(String property) {
		return (ArrayList<?>)this.config.get(property);
	}
	@SuppressWarnings("unchecked")
	public ArrayList<String> getListString(String property) {
		return (ArrayList<String>)this.config.get(property);
	}
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getListNumber(String property) {
		return (ArrayList<Double>)this.config.get(property);
	}
	public String getString(String property) {
		return (String)this.config.get(property);
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		saveConfig(baos);
		return new String(baos.toByteArray(), StandardCharsets.US_ASCII);
	}

}
