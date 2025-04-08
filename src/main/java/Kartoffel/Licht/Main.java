package Kartoffel.Licht;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.joml.SimplexNoise;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK13;

import Kartoffel.Licht.InventoryDisplay.progressBar;
import Kartoffel.Licht.AGGraphics.AGDrawer;
import Kartoffel.Licht.AGGraphics.AGShader;
import Kartoffel.Licht.AGGraphics.AGTexture;
import Kartoffel.Licht.AGGraphics.AGVGraphics;
import Kartoffel.Licht.AGGraphics.AGVGraphics.Window;
import Kartoffel.Licht.AGGraphics.EXTAWT.AWTIntegration;
import Kartoffel.Licht.Vulkan.Buffer;
//import Kartoffel.Licht.AGGraphics.EXTImGui.ImGuiIntegration;
import Kartoffel.Licht.Vulkan.Descriptor;
import Kartoffel.Licht.Vulkan.DescriptorPool;
import Kartoffel.Licht.Vulkan.DescriptorPool.DescriptorSet;
import Kartoffel.Licht.Vulkan.GraphicsPipeline.GraphicsPipelineInfo;
import Kartoffel.Licht.Vulkan.GraphicsPipeline.GraphicsPipelineInfo.Scissor;
import Kartoffel.Licht.Vulkan.GraphicsPipeline.GraphicsPipelineInfo.Viewport;
import Kartoffel.Licht.Vulkan.PipelineLayout.PushConstant;
import Kartoffel.Licht.Vulkan.Sampler;
import Kartoffel.Licht.Vulkan.VertexAttribDescriptor;
//import imgui.ImGui;
//import imgui.flag.ImGuiConfigFlags;

public class Main {
	
	public static void main(String[] args){
		try {
			main();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while starting up this game: " + e.getLocalizedMessage());
			System.exit(-1);
		}
	}
	
	//GAME############################################

	public static final String CONFIGPROP_HIGHSCORE_POINTS = "Highscore_points";
	public static final String CONFIGPROP_HIGHSCORE_TIME = "Highscore_time";
	
	public static final String WORLD_MAINMENU = "MainMenu";
	public static final String WORLD_LOADING = "loading";
	public static final String WORLD_GAME = "Game";
	public static final String WORLD_TUTORIAL = "Tutorial";
	
	public static float ASPECT_RATIO = 16.0f/9.0f;
	public static float WORLD_WIDTH = 16; //Also defined in shader code
	public static float WORLD_HEIGHT = 9;
	
	private static Config config;
	private static AGVGraphics graphics;
	private static Window window;
	
	private static AGDrawer drawer;
	
//	private static ImGuiIntegration imguiIn;
	
	private static AGTexture t;
	private static AGShader mainShader;
	private static DescriptorPool mainShaderUniforms;
	
	public final static double MUSIC_BASE_MILLIS = 2083.3333;
	public static double TIME_EXTREME = 60*6.5f; //Normal
	
	public static double TIME_RAYS = 30;
	public static double TIME_MOVEMENT = 90;
	public static double TIME_ROTATION = 120;
	
	public static double RAYS_INIT_delay = 1;
	
	public static double MOVEMENT_SWITCH_PERIOD = Math.PI*2*(1/30);
	
	public static double RAYS_INIT_delayIncrease = 1/60.0;
	public static double MOVEMENT_INIT_delayIncrease = 1.0/180;
	public static double ROTATION_INIT_delayIncrease = 1.0/120;
	
	public static float[] SEASON_MOD_LASER = new float[] {1, 0.5f, 1};
	public static int[] SEASON_MOD_MOLD = new int[] {Integer.MAX_VALUE, 4, 8};
	public static float[] SEASON_MOD_SPIN = new float[] {0, 0.01f, 1.2f};
	public static float[][] SEASON_TINT = new float[][] {{0.5f, 0.1f, 0.9f}, {0.1f, 0.8f, 0.8f}, {0.8f, 0.7f, 0.6f}};
	
	public static int calculateSeason(double time) {
		float b = (float) (time/60.0);
		float a = SimplexNoise.noise(b, -b)*.5f+.5f;
		return (int) (a*3*Math.min(b, 1));
	}
	
	private static int GAMEMODE = 0;
	
	public static boolean loadGamemode(int m) {
		if(m == 0) { //TODO make a lot easier
			TIME_EXTREME = 60*9f;
			PlatformSpawner.SPAWN_DELAY = (int) (Main.MUSIC_BASE_MILLIS*0.05);
			Platform.DISSAPEAR_TIME = 3;
			PlatformSpawner.DESPAWN_CHANCE = 0.1f;
			PlatformSpawner.SEPERATION = 2;
			PlatformSpawner.M_PLATFORM_SIZE = 1;
			TIME_RAYS = 33;
			TIME_MOVEMENT = 58; //43 a 33
			TIME_ROTATION = 180;
			RAYS_INIT_delay = MUSIC_BASE_MILLIS*0.002;
			RAYS_INIT_delayIncrease = 1/5000.0;
			MOVEMENT_SWITCH_PERIOD = Math.PI*2*(1/50);
			Player.MAX_AIR_TIME = 2;
			Player.MAX_POINTS = 150;
			Player.ABB_DISABLED = false;
			Player.ABC_DISABLED = false;
			Player.ABD_DISABLED = false;
			Tentacle.IMPACT_TIME = 2;
			return true;
		}
		if(m == 1) { //TODO make a bit easier
			TIME_EXTREME = 60*6.5f;
			PlatformSpawner.SPAWN_DELAY = (int) (Main.MUSIC_BASE_MILLIS*0.05);
			Platform.DISSAPEAR_TIME = 2;
			PlatformSpawner.DESPAWN_CHANCE = 0.2f;
			PlatformSpawner.SEPERATION = 1;
			PlatformSpawner.M_PLATFORM_SIZE = 1;
			TIME_RAYS = 33;
			TIME_MOVEMENT = 58; //43 a 33
			TIME_ROTATION = 180;
			RAYS_INIT_delay = MUSIC_BASE_MILLIS*0.001;
			RAYS_INIT_delayIncrease = 1/2000.0;
			MOVEMENT_SWITCH_PERIOD = Math.PI*2*(1/30);
			Player.MAX_AIR_TIME = 1.2;
			Player.MAX_POINTS = 100;
			Player.ABB_DISABLED = false;
			Player.ABC_DISABLED = false;
			Player.ABD_DISABLED = false;
			Tentacle.IMPACT_TIME = 1;
			return true;
		}
		if(m == 2) { //TODO Make a bit harder
			TIME_EXTREME = 60*4f;
			PlatformSpawner.SPAWN_DELAY = (int) (Main.MUSIC_BASE_MILLIS*0.05);
			Platform.DISSAPEAR_TIME = 1;
			PlatformSpawner.DESPAWN_CHANCE = 1f;
			PlatformSpawner.SEPERATION = 0;
			PlatformSpawner.M_PLATFORM_SIZE = 1;
			TIME_RAYS = 0;
			TIME_MOVEMENT = 33; //43 a 33
			TIME_ROTATION = 58;
			RAYS_INIT_delay = MUSIC_BASE_MILLIS*0.0008;
			RAYS_INIT_delayIncrease = 1/1000.0;
			MOVEMENT_SWITCH_PERIOD = Math.PI*2*(1/20);
			Player.MAX_AIR_TIME = 0.75;
			Player.MAX_POINTS = 65;
			Player.ABB_DISABLED = true;
			Player.ABC_DISABLED = false;
			Player.ABD_DISABLED = false;
			Tentacle.IMPACT_TIME = 0.7f;
			return true;
		}
		return false;
	}
	
	static boolean t_isDyning = false;
	static boolean t_isJoining = false;
	static boolean l_isfullscreen = false;
	static boolean t_isChangingPhase = false;
	static long ABILITY_TIMEFRZ_TSTMP = 0;
	
	public static List<Thread> threads = new ArrayList<Thread>();
	
	public static boolean isPlayerAlive() {
		for(Player p : players)
			if(!p.isDead())
				return true;
		return false;
	}
	
//	private static AGShader bloomShader;
//	private static AGShader blurrShader;
//	private static DescriptorPool blurrShader_dpool;
//	private static DescriptorSet blurrShader_ds_bufferA;
//	private static DescriptorSet blurrShader_ds_bufferB;
//	private static DescriptorSet blurrShader_ds_bufferC;
	
//	private static AGShader compositeShader;
//	private static DescriptorPool compositeShader_dpool;
//	private static DescriptorSet compositeShader_ds_bufferA; //Not neseccary!
//	private static DescriptorSet compositeShader_ds_bufferB;
//	private static DescriptorSet compositeShader_ds_bufferC;
	
//	private static AGCanvasImage bufferA; //Where source image is
//	private static AGTexture bufferA_tex;
//	private static AGCanvasImage bufferB; //Blurr pingpong A
//	private static AGTexture bufferB_tex;
//	private static AGCanvasImage bufferC; //Blurr pingpong B
//	private static AGTexture bufferC_tex;
	
	
	private static Buffer UNIFORM_BUFFER;
	
	public static float GLOBAL_ROTATION = 0;
	public static float GLOBAL_ROTATIONsn = 0;
	public static float GLOBAL_ROTATIONcs = 1;
	public static void setGlobalRotation(float v) {
		GLOBAL_ROTATION = v;
		GLOBAL_ROTATIONsn = (float) Math.sin(v);
		GLOBAL_ROTATIONcs = org.joml.Math.cosFromSin(GLOBAL_ROTATIONsn, v);
	}
	public static void setGlobalTint(float r, float g, float b, float a) {
		graphics.getUtils().putBufferDirect(UNIFORM_BUFFER, new float[] {r, g, b, a}, 0, 0, 4);
	}
	public static Entity[] secondPhaseBackgrounds;
	static int current_phase = 0;
	public static void setPhase(int phase) {
		if(t_isChangingPhase)
			return;
		if(phase == current_phase)
			return;
		current_phase = phase;
		t_isChangingPhase = true;
		pfs.phase = phase;
		threads.add(Thread.startVirtualThread(()->{
			try {
				long t2 = System.currentTimeMillis();
				while(t2 > System.currentTimeMillis()-1000) {
					for(int i = 0; i < secondPhaseBackgrounds.length; i++) {
						if(i == phase)
							secondPhaseBackgrounds[i].a =  Math.max(Math.min(1, (System.currentTimeMillis()-t2)/1000.0f), secondPhaseBackgrounds[i].a);
						else
							secondPhaseBackgrounds[i].a = Math.min(Math.max(0, 1-(System.currentTimeMillis()-t2)/1000.0f), secondPhaseBackgrounds[i].a);
						secondPhaseBackgrounds[i].changed = true;
					}
					Thread.sleep(20);
				}
				for(int i = 0; i < secondPhaseBackgrounds.length; i++)
					if(i != phase) {
						secondPhaseBackgrounds[i].a = 0;
						secondPhaseBackgrounds[i].changed = true;
					}
				t_isChangingPhase = false;
			} catch (InterruptedException e) {
				
			}
		}));
	}
	
	
	private static World world;
	
	public static List<Player> players = new ArrayList<Player>();
	private static Entity dummy;
	
	private static DescriptorPool texturePool;
	private static Sampler sampler;
	private static List<AGTexture> textures = new ArrayList<>();
	private static HashMap<String, DescriptorSet> texturesM = new HashMap<String, DescriptorSet>();
	
//	public static InputMethod INPUT_KEYBOARD;
	
	public static Random RANDOM = new Random();
	
	public static List<InputMethod> potentialInputMethods = new ArrayList<InputMethod>();
	public static List<InputMethod> readyInputMethods = new ArrayList<InputMethod>();
	
	public static PlatformSpawner pfs;
	
	static double worldMoveAngle = 0;
	public static DescriptorSet MAINSHADER_UNIFORMS;
//	public static MidiChannel midiChannel;
	static volatile boolean shouldReset = false;
	public static void resetWorld() {
		shouldReset = true;
	}
	
	public static void toggleFullscreen() {
		l_isfullscreen = !l_isfullscreen;
		var a = GLFW.glfwGetMonitors();
		long moni = 0;
		int[] xx = new int[1];
		int[] yy = new int[1];
		int[] xx2 = new int[1];
		int[] yy2 = new int[1];
		int[] r = window.getPosition();
		for(int i = 0; i < a.capacity(); i++) {
			long c = a.get(i);
			GLFW.glfwGetMonitorWorkarea(c, xx, yy, xx2, yy2);
			if((xx[0] <= r[0] && xx[0]+xx2[0] >= r[0])&&(xx[0] <= r[0] && xx[0]+xx2[0] >= r[0])) {
				moni = c;
				break;
			}
			
		}
		if(moni == 0)
			moni = a.get(0);
		if(l_isfullscreen)
			GLFW.glfwSetWindowMonitor(window.getGlfwHandle(), moni, 0, 0, xx2[0], yy2[0], -1);
		else
			GLFW.glfwSetWindowMonitor(window.getGlfwHandle(), 0, xx2[0]/4, yy2[0]/4, xx2[0]/2, yy2[0]/2, -1);
	}
	
	public static void main() throws Throwable {
		{
			config = Config.load("KFLProject.ini", true);
			if(config == null) { //Explorer already active
				JOptionPane.showMessageDialog(null, "Game already open!");
				return;
			}
			config.setString("Description", "Game config file for Ludum Dare #57");
			config.defaultNumber(CONFIGPROP_HIGHSCORE_POINTS, 0);
			config.defaultNumber(CONFIGPROP_HIGHSCORE_TIME, 0);
		}
		AGVGraphics.addDeviceFeature("sampleRateShading");
		graphics = AGVGraphics.create(null, null, 0);
		window = graphics.createWindow(0, false);
		GLFW.glfwSetWindowTitle(window.getGlfwHandle(), "Ludum Dare 57 Game Placeholder title");
		
		drawer = graphics.createDrawer();
		
		{
			t = graphics.generateTexture2D(VK13.VK_FORMAT_R8G8B8A8_SRGB, 500, 500);
			graphics.transitionTexture(t, VK13.VK_IMAGE_LAYOUT_GENERAL,
					VK13.VK_ACCESS_SHADER_READ_BIT, 
					VK13.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT);
			ByteBuffer b = MemoryUtil.memCalloc(500*500*4);
			for(int i = 0; i < b.capacity(); i+=4) {
				b.put(i+0, (byte) (Math.cos(i/5000.0+55)*128));
				b.put(i+1, (byte) (Math.sin(i/4000.0)*128));
				b.put(i+2, (byte) (Math.cos(i/2000.0+10)*128));
				b.put(i+3, (byte) -1);
			}
			graphics.putTexture2D(t, b, 500, 500, 0, 0);
			MemoryUtil.memFree(b);
		}
		{ //Graphics
			
			{
				sampler = graphics.generateTextureSampler(false, false, VK13.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE, VK13.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE, VK13.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE, 0);
			}
			
//			AGCanvasDisplay mainCanvas = window.getCanvas();
//			final int TYPE = VK13.VK_FORMAT_R8G8B8A8_SNORM;
//			bufferA_tex = graphics.generateTexture2D(TYPE, mainCanvas.getWidth(), mainCanvas.getHeight(), VK13.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT|VK13.VK_IMAGE_USAGE_SAMPLED_BIT, 0, 0, VK13.VK_IMAGE_ASPECT_COLOR_BIT);
//			bufferA = graphics.createCanvas(mainCanvas.getWidth(), mainCanvas.getHeight(), new renderAttachment[] {new renderAttachment(TYPE)}, new AGTexture[][] {{bufferA_tex}},
//					new subpass[] {new subpass(new Reference[] {new Reference(0)}, null)});
//			graphics.transitionTexture(bufferA_tex, VK13.VK_IMAGE_LAYOUT_GENERAL,
//					VK13.VK_ACCESS_SHADER_READ_BIT|VK13.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT, 
//					VK13.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT|VK13.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
//			bufferB_tex = graphics.generateTexture2D(TYPE, mainCanvas.getWidth(), mainCanvas.getHeight(), VK13.VK_IMAGE_USAGE_STORAGE_BIT|VK13.VK_IMAGE_USAGE_SAMPLED_BIT, 0, 0, VK13.VK_IMAGE_ASPECT_COLOR_BIT);
//			bufferB = graphics.createCanvas(mainCanvas.getWidth(), mainCanvas.getHeight(), new renderAttachment[] {new renderAttachment(TYPE)}, new AGTexture[][] {{bufferB_tex}},
//					new subpass[] {new subpass(new Reference[] {new Reference(0)}, null)});
//			graphics.transitionTexture(bufferB_tex, VK13.VK_IMAGE_LAYOUT_GENERAL,
//					VK13.VK_ACCESS_SHADER_READ_BIT|VK13.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT, 
//					VK13.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT|VK13.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
//			bufferC_tex = graphics.generateTexture2D(TYPE, mainCanvas.getWidth(), mainCanvas.getHeight(), VK13.VK_IMAGE_USAGE_STORAGE_BIT|VK13.VK_IMAGE_USAGE_SAMPLED_BIT, 0, 0, VK13.VK_IMAGE_ASPECT_COLOR_BIT);
//			bufferC = graphics.createCanvas(mainCanvas.getWidth(), mainCanvas.getHeight(), new renderAttachment[] {new renderAttachment(TYPE)}, new AGTexture[][] {{bufferC_tex}},
//					new subpass[] {new subpass(new Reference[] {new Reference(0)}, null)});
//			graphics.transitionTexture(bufferC_tex, VK13.VK_IMAGE_LAYOUT_GENERAL,
//					VK13.VK_ACCESS_SHADER_READ_BIT|VK13.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT, 
//					VK13.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT|VK13.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
			
			mainShader = graphics.createShaderPipeline(loadResourceString("ui.vert"), loadResourceString("ui.frag"),
					new Descriptor[][] {{
						new Descriptor(0, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VK13.VK_SHADER_STAGE_FRAGMENT_BIT)
					},{
						new Descriptor(0, VK13.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, VK13.VK_SHADER_STAGE_FRAGMENT_BIT)
					}
			}, new PushConstant[] {new PushConstant(VK13.VK_SHADER_STAGE_VERTEX_BIT, 0, 4*2)}, new GraphicsPipelineInfo(VK13.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
							new int[] {VK13.VK_DYNAMIC_STATE_VIEWPORT, VK13.VK_DYNAMIC_STATE_SCISSOR},
							new Viewport[] {new Viewport()},
							new Scissor[] {new Scissor()},
							new VertexAttribDescriptor[] {
									new VertexAttribDescriptor(0, 0, VK13.VK_FORMAT_R32G32_SFLOAT, 0),
									new VertexAttribDescriptor(0, 1, VK13.VK_FORMAT_R32G32_SFLOAT, 4*2),
									new VertexAttribDescriptor(0, 2, VK13.VK_FORMAT_R32G32B32A32_SFLOAT, 4*4)
							},
							8*4).setDepthStencil_depthCompareOp(VK13.VK_COMPARE_OP_ALWAYS)
							  .setRasterization_cullMode(VK13.VK_CULL_MODE_NONE),
					window.getCanvas().getRenderpass());
			texturePool = mainShader.createDescriptorPool(0, 50);
			UNIFORM_BUFFER = graphics.getUtils().generateUniformBuffer(4*4, false);
			mainShaderUniforms = mainShader.createDescriptorPool(1, 1);
			MAINSHADER_UNIFORMS = graphics.createDescriptorSets(mainShaderUniforms, mainShader.getDescriptorLayouts()[1])[0];
			graphics.setDataOfDescriptorSet(MAINSHADER_UNIFORMS, AGVGraphics.db(UNIFORM_BUFFER, VK13.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER));
//			String postvert = loadResourceString("post.vert");
//			bloomShader = graphics.createShaderPipeline(postvert, loadResourceString("bloom.frag"),
//					new Descriptor[][] {{
//						new Descriptor(0, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VK13.VK_SHADER_STAGE_FRAGMENT_BIT)
//					}}, new PushConstant[] {}, new GraphicsPipelineInfo(VK13.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
//							new int[] {VK13.VK_DYNAMIC_STATE_VIEWPORT, VK13.VK_DYNAMIC_STATE_SCISSOR},
//							new Viewport[] {new Viewport()},
//							new Scissor[] {new Scissor()},
//							new VertexAttribDescriptor[] {},
//							8*4).setDepthStencil_depthCompareOp(VK13.VK_COMPARE_OP_ALWAYS)
//							  .setRasterization_cullMode(VK13.VK_CULL_MODE_NONE),
//					window.getCanvas().getRenderpass());
//			blurrShader = graphics.createShaderPipeline(postvert, loadResourceString("blurr.frag"),
//					new Descriptor[][] {{
//						new Descriptor(0, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VK13.VK_SHADER_STAGE_FRAGMENT_BIT)
//					}}, new PushConstant[] {}, new GraphicsPipelineInfo(VK13.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
//							new int[] {VK13.VK_DYNAMIC_STATE_VIEWPORT, VK13.VK_DYNAMIC_STATE_SCISSOR},
//							new Viewport[] {new Viewport()},
//							new Scissor[] {new Scissor()},
//							new VertexAttribDescriptor[] {},
//							8*4).setDepthStencil_depthCompareOp(VK13.VK_COMPARE_OP_ALWAYS)
//							  .setRasterization_cullMode(VK13.VK_CULL_MODE_NONE),
//					window.getCanvas().getRenderpass());
//			blurrShader_dpool = blurrShader.createDescriptorPool(0, 3);
//			blurrShader_ds_bufferA = graphics.createDescriptorSets(blurrShader_dpool, blurrShader.getDescriptorLayouts()[0])[0];
//			graphics.setDataOfDescriptorSet(blurrShader_ds_bufferA, AGVGraphics.di(bufferA_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER));
//			blurrShader_ds_bufferB = graphics.createDescriptorSets(blurrShader_dpool, blurrShader.getDescriptorLayouts()[0])[0];
//			graphics.setDataOfDescriptorSet(blurrShader_ds_bufferB, AGVGraphics.di(bufferB_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER));
//			blurrShader_ds_bufferC = graphics.createDescriptorSets(blurrShader_dpool, blurrShader.getDescriptorLayouts()[0])[0];
//			graphics.setDataOfDescriptorSet(blurrShader_ds_bufferC, AGVGraphics.di(bufferC_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER));
			
//			compositeShader = graphics.createShaderPipeline(postvert, loadResourceString("composite.frag"),
//					new Descriptor[][] {{
//						new Descriptor(0, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VK13.VK_SHADER_STAGE_FRAGMENT_BIT)
//					}}, new PushConstant[] {}, new GraphicsPipelineInfo(VK13.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
//							new int[] {VK13.VK_DYNAMIC_STATE_VIEWPORT, VK13.VK_DYNAMIC_STATE_SCISSOR},
//							new Viewport[] {new Viewport()},
//							new Scissor[] {new Scissor()},
//							new VertexAttribDescriptor[] {},
//							8*4).setDepthStencil_depthCompareOp(VK13.VK_COMPARE_OP_ALWAYS)
//							  .setRasterization_cullMode(VK13.VK_CULL_MODE_NONE),
//					window.getCanvas().getRenderpass());
//			compositeShader = graphics.createComputeShaderPipeline(ShaderStage.of(graphics.createShaderModule(loadResourceString("composite.cs"), "composite.cs", "main", VK13.VK_SHADER_STAGE_COMPUTE_BIT), "main", VK13.VK_SHADER_STAGE_COMPUTE_BIT),
//					new Descriptor[][] {{
//						new Descriptor(0, VK13.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, VK13.VK_SHADER_STAGE_COMPUTE_BIT),
//						new Descriptor(1, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VK13.VK_SHADER_STAGE_COMPUTE_BIT)
//					}},
//					new PushConstant[0]);
//			compositeShader_dpool = compositeShader.createDescriptorPool(0, 3);
//			compositeShader_ds_bufferA = graphics.createDescriptorSets(compositeShader_dpool, compositeShader.getDescriptorLayouts()[0])[0];
//			graphics.setDataOfDescriptorSet(compositeShader_ds_bufferA, AGVGraphics.di(bufferA_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER), AGVGraphics.di(bufferA_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER));
//			compositeShader_ds_bufferB = graphics.createDescriptorSets(compositeShader_dpool, compositeShader.getDescriptorLayouts()[0])[0];
//			graphics.setDataOfDescriptorSet(compositeShader_ds_bufferB, AGVGraphics.di(bufferB_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER), AGVGraphics.di(bufferA_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER));
//			compositeShader_ds_bufferC = graphics.createDescriptorSets(compositeShader_dpool, compositeShader.getDescriptorLayouts()[0])[0];
//			graphics.setDataOfDescriptorSet(compositeShader_ds_bufferC, AGVGraphics.di(bufferC_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER), AGVGraphics.di(bufferA_tex, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER));
			
		}
		{ //Resource preloading
			Main.getSet("white.png");
			Main.getSet("background_main.png");
			Main.getSet("ui/button_play.png");
			Main.getSet("ui/loading_audio.png");
			Main.getSet("ui/symbols.png");
		}
		
		potentialInputMethods.add(new KeyboardInputMethod(window.getGlfwHandle()));
		potentialInputMethods.add(new KeyboardInputMethod2(window.getGlfwHandle()));
		potentialInputMethods.add(new GamepadInputMethod(window.getGlfwHandle(), 0));
		potentialInputMethods.add(new GamepadInputMethod(window.getGlfwHandle(), 1));
		
		world = new World(graphics.getUtils(), drawer, 10);
		
		loadAndStart();
		
//		ImGui.createContext();
//		ImGui.getIO().setConfigFlags(ImGuiConfigFlags.DockingEnable);//ImGuiConfigFlags.DockingEnable currently causes bugs with viewports
//		imguiIn = new ImGuiIntegration(window);
		
		while(!GLFW.glfwWindowShouldClose(window.getGlfwHandle())) {
			GLFW.glfwPollEvents();
			int[] windowSize = window.getSize();
			if(drawer.ready() && (windowSize[0]!=0&&windowSize[1]!=0)) {
				window.resizeDisplayBuffers();
				int imageIndex = window.getCanvas().updateImageIndex(Long.MAX_VALUE); //Limit to refresh rate for GUI-applications.
				if(imageIndex > -1) { //When actually drawable
					int[] size = window.getSize();
					float scal = Math.min(size[0]/16.0f, size[1]/9.0f);
					float width = 16*scal;
					float height = 9*scal;
					float offx = (size[0]-width)/2;
					float offy = (size[1]-height)/2;
					renderGUI();
//					System.out.println("startD");
//					drawer.startDraw();//###################################
////						drawer.transitionImageLayout(bufferA_tex, VK13.VK_IMAGE_LAYOUT_GENERAL,
////								VK13.VK_ACCESS_SHADER_READ_BIT, 
////								VK13.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT);
//						drawer.beginRenderpass(bufferA, 0, 0, 0, window.getContentWidth(), window.getContentHeight());
//						{
//							drawer.setViewport(0, offx, offy, width, height, 0, 1);
//							drawer.setScissor(0, (int)offx, (int)offy, (int)width, (int)height);
//							drawer.bindShader(mainShader);
//							world.rerecordDrawBuffer(mainShader);
//						}
//						drawer.endRenderpass();
//					drawer.endDraw();//###################################
//					drawer.submitAll(0);
//					drawer.join();
//					System.out.println("Start 2");
					drawer.startDraw();//###################################
//						drawer.transitionImageLayout(bufferA_tex, VK13.VK_IMAGE_LAYOUT_GENERAL,
//								VK13.VK_ACCESS_SHADER_READ_BIT, 
//								VK13.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT);
						
						drawer.beginRenderpass(window.getCanvas(), imageIndex, 0, 0, window.getContentWidth(), window.getContentHeight());
						{
							drawer.setViewport(0, offx, offy, width, height, 0, 1);
							drawer.setScissor(0, (int)offx, (int)offy, (int)width, (int)height);
							drawer.bindShader(mainShader);
							world.rerecordDrawBuffer(mainShader);
						}
//						drawer.bindShader(compositeShader);
//						drawer.bindDescriptorSets(compositeShader, compositeShader_ds_bufferA);
//						drawer.draw(6, 1);
//						if(imguiIn.drawable())
//							imguiIn.drawMain(drawer);
						drawer.endRenderpass();
						
					drawer.endDraw();//###################################
					window.getCanvas().submitDisplay(drawer, graphics.getMainQueue());
					if(shouldReset)
						loadAndStart();
					shouldReset = false;
//					System.out.println("Submit!");
				}
			}
		}
		for(Thread t : threads)
			t.interrupt();
		SoundUtils.free();
		drawer.join(); //Wait to avoid cleanup while 'last frame' is in process
		graphics.waitUntilIdle();
//		imguiIn.free();
		world.free();
		mainShader.free();
		t.free();
		
		texturePool.free();
		for(AGTexture t : textures)
			t.free();
		
		sampler.free();
		
		drawer.free();
		mainShaderUniforms.free();
		UNIFORM_BUFFER.free();
		
		graphics.free();
		GLFW.glfwTerminate();
		Thread.sleep(1000); //Let vulkan go to bed
		System.exit(0); //Fixes bugs with sequencer
	}
	private static void loadAndStart() throws Throwable {
		for(Thread t : threads)
			t.interrupt();
		threads.clear();
		players.clear();
		System.out.println("Resetting..");
		drawer.join();
		SoundUtils.reset();
		world.reset();
		PlayerListDisplay playerLD = new PlayerListDisplay(readyInputMethods, world);
		{ //World Building
//			final DescriptorSet white = getSet("white.png");
			world.switchLevelNOW(WORLD_MAINMENU);
			world.addEntityR(new Entity(0, 0, WORLD_WIDTH, WORLD_HEIGHT) {
				{
					this.tw = 1.0f/6;
					this.tx = 0.0f/6;
					this.r = 0.2f;
					this.g = 0.2f;
					this.b = 0.2f;
				}
				double time = 0;
				int a = 0;
				public void update(double delta) {
					time += delta;
					if(time > 3.46) {
						time = 0;
						a++;
						this.tx = (a/6.0f)%1;
						this.changed = true;
					}
				};
				
			}.setTexture(getSet("textures/tutorial.png")));
			Button startButton = new Button(()->{
				if(t_isJoining)
					return;
				t_isJoining = true;
				System.out.println("Starting Run...");
				threads.add(Thread.startVirtualThread(() ->{
					loadGamemode(GAMEMODE);
					try {
						final long t = System.currentTimeMillis();
						SoundUtils.startGameMusic(()->{
							//Do something
							float a = Math.max(1-(System.currentTimeMillis()-t)/1000.0f, 0);
							setGlobalTint(a, a, a, 1);
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						});
						world.loadLevel(WORLD_GAME);
						int i = 0;
						for(InputMethod m : readyInputMethods) {
							Player p = (Player) new Player(m, pfs, playerLD.skinIndex.get(m));
							players.add(p);
							world.addEntity(p);
							InventoryDisplay id = new InventoryDisplay(i, p, world);
							world.addEntity(id);
							i++;
						}
						final long t2 = System.currentTimeMillis();
						while(t2 > System.currentTimeMillis()-500) {
							float a = Math.min((System.currentTimeMillis()-t2)/500.0f, 1);
							setGlobalTint(a, a, a, 1);
							Thread.sleep(20);
						}
					} catch (InterruptedException | RuntimeException e) {}
				}));
			});
			final DescriptorSet[] diffs = new DescriptorSet[] {getSet("ui/easy.png"), getSet("ui/medium.png"), getSet("ui/hard.png")};
			final Button difficultyButton = new ToggleButton((a, b)->{
					b.texture = diffs[a];
					SoundUtils.playDiffSound(a);
					GAMEMODE = a;
			}, 3);
			difficultyButton.y += 7;
			difficultyButton.x -= 4;
			difficultyButton.w = 3;
			difficultyButton.h = 1.5f;
			startButton.w = 5;
			startButton.h = 4f;
			Button optionsButton2 = new Button(()->{
				toggleFullscreen();
			});
			optionsButton2.y += 7;
			optionsButton2.x += WORLD_WIDTH-2;
			optionsButton2.w = 1.5f;
			optionsButton2.h = 1.5f;
			Button buttonquitt = new Button(()->{
				GLFW.glfwSetWindowShouldClose(window.getGlfwHandle(), true);
			});
			buttonquitt.y += 7;
			buttonquitt.x -= WORLD_WIDTH-2;
			buttonquitt.w = 1.5f;
			buttonquitt.h = 1.5f;
			startButton.setTexture(getSet("ui/button_play.png"));
			optionsButton2.setTexture(getSet("ui/screenswitch.png"));
			buttonquitt.setTexture(getSet("ui/button_exit.png"));
			difficultyButton.setTexture(getSet("ui/easy.png"));
			Button tutorialButton = new Button(()->{
				world.loadLevel(WORLD_TUTORIAL);
			});
			tutorialButton.y += 7;
			tutorialButton.x += 4;
			tutorialButton.w = 4;
			tutorialButton.h = 1;
			tutorialButton.texture = getSet("ui/button_tutorial.png");
			world.addEntityR(tutorialButton);
			world.addEntityR(startButton);
			world.addEntityR(difficultyButton);
			world.addEntityR(optionsButton2);
			world.addEntityR(buttonquitt);
			UIManager  manager = new UIManager(startButton, window.getGlfwHandle());
			manager.addButton(difficultyButton);
			manager.addButton(optionsButton2);
			manager.addButton(tutorialButton);
			manager.addButton(buttonquitt);
			manager.methods = readyInputMethods;
			
			NumberE higscorePoints = new NumberE(world);
			higscorePoints.val = config.getInt(CONFIGPROP_HIGHSCORE_POINTS);
			higscorePoints.orientation = 0;
			higscorePoints.x = -WORLD_WIDTH+higscorePoints.getW();
			higscorePoints.y = -WORLD_HEIGHT+higscorePoints.h;
			world.addEntityR(higscorePoints);
			
			NumberE higscoreTime = new NumberE(world);
			higscoreTime.val = config.getInt(CONFIGPROP_HIGHSCORE_TIME);
			higscoreTime.orientation = 0;
			higscoreTime.x = -WORLD_WIDTH+higscoreTime.getW();
			higscoreTime.y = -WORLD_HEIGHT+higscoreTime.h+higscorePoints.h*2;
			world.addEntityR(higscoreTime);
			
			Entity dummy = new Entity() {
				@Override
				public void update(double delta) {
					potentialInputMethods.forEach((a)->{
						if(readyInputMethods.contains(a))
							return;
						if(a.isPresent()) {
							readyInputMethods.add(a);
							System.out.println("Added new input Method: " + a.getClass());
						}
					});
				}
			};
			dummy.visible = false;
			world.addEntityR(dummy);
			
			world.addEntityR(manager);
			
			world.addEntityR(playerLD);
			playerLD.x = -WORLD_WIDTH+playerLD.w;
			playerLD.y = -WORLD_HEIGHT+higscoreTime.h*2+higscorePoints.h*2+playerLD.h;
		}
		{
			world.switchLevelNOW(WORLD_TUTORIAL);
			
			final Entity bck = new Entity(0, 0, WORLD_WIDTH, WORLD_HEIGHT).setTexture(getSet("textures/tutorial.png"));
			final int[] cnt = new int[] {0};
			final boolean[] cntQFIX = new boolean[] {false};
			final int PAGES = 6;
			bck.w = WORLD_WIDTH*PAGES;
			bck.h = WORLD_HEIGHT;
			bck.x = bck.w-WORLD_WIDTH*(cnt[0])*2-WORLD_WIDTH;
			Button next = new Button(()->{
				if(!cntQFIX[0]) {
					cntQFIX[0] = true;
					return;
				}
				cnt[0] = Math.min(cnt[0]+1, PAGES);
				bck.x = bck.w-WORLD_WIDTH*(cnt[0])*2-WORLD_WIDTH;
				if(cnt[0] >= PAGES) {
					cnt[0] = 0;
					bck.x = bck.w/2+WORLD_WIDTH*2;
					world.loadLevel(WORLD_MAINMENU);
				}
				bck.changed = true;
			});
			next.x = WORLD_WIDTH-next.w*2;
			next.y = WORLD_HEIGHT-next.h*2;
			next.texture = getSet("textures/next.png");
			Button prev = new Button(()->{
				cnt[0] = Math.max(cnt[0]-1, 0);
				bck.x = bck.w-WORLD_WIDTH*(cnt[0])*2-WORLD_WIDTH;
				bck.changed = true;
			});
			prev.texture = getSet("textures/prev.png");
			prev.x = prev.w*2-WORLD_WIDTH;
			prev.y = WORLD_HEIGHT-prev.h*2;
			UIManager m = new UIManager(next, window.getGlfwHandle());
			m.addButton(prev);
			m.methods = readyInputMethods;
			world.addEntityR(bck);
			world.addEntityR(next);
			world.addEntityR(prev);
			world.addEntityR(m);
		}
		{
			world.switchLevelNOW(WORLD_GAME);
			secondPhaseBackgrounds = new Entity[3];
			world.addEntityR(secondPhaseBackgrounds[0] = new Entity(0, 0, WORLD_WIDTH, WORLD_HEIGHT).setTexture(getSet("background_s1.png")));
			world.addEntityR(secondPhaseBackgrounds[1] = new Entity(0, 0, WORLD_WIDTH, WORLD_HEIGHT).setTexture(getSet("background_s2.png")));
			world.addEntityR(secondPhaseBackgrounds[2] = new Entity(0, 0, WORLD_WIDTH, WORLD_HEIGHT).setTexture(getSet("background_s3.png")));
			pfs = new PlatformSpawner(world);
			world.addEntityR(pfs);
			dummy = new Entity() {
				double k = 0;
				double tt = 0;
				double moverot = 0;
				{
					Main.ABILITY_TIMEFRZ_TSTMP = System.currentTimeMillis();
				}
				@Override
				public void update(double delta) {
					if(System.currentTimeMillis() < ABILITY_TIMEFRZ_TSTMP)
						return;
					tt += delta;
					setPhase(calculateSeason(tt));
					float laserMod = Main.SEASON_MOD_LASER[current_phase]; //Percentage of delay
					pfs.MOLD_SPAWN_CHANCE = Main.SEASON_MOD_MOLD[current_phase];
					float spinMod = Main.SEASON_MOD_SPIN[current_phase];
//					world.setMoveWorld(Math.cos(worldMoveAngle)*2, Math.sin(worldMoveAngle)*2);
					if(tt > TIME_ROTATION) 
						setGlobalRotation(GLOBAL_ROTATION+(float) (delta*SimplexNoise.noise((float) tt, (float) tt)*(tt-TIME_ROTATION)*ROTATION_INIT_delayIncrease)+(float)(delta*spinMod));
					if(tt > TIME_MOVEMENT) {
						double inc = (tt-TIME_MOVEMENT)*MOVEMENT_INIT_delayIncrease;
						double a = Math.max(Math.cos(tt*MOVEMENT_SWITCH_PERIOD), 0);
						moverot += tt*a;
						world.worldMoveDX = Math.cos(moverot)*inc;
						world.worldMoveDY = Math.sin(moverot)*inc;
					}
					k += delta;
					if(tt > TIME_RAYS && k > RAYS_INIT_delay/(1+(tt-TIME_RAYS)*RAYS_INIT_delayIncrease)*laserMod) {
						k = 0;
						var tenti = new Tentacle(players);
						tenti.rot = (float) RANDOM.nextDouble(6.2);
						Player rpp = players.get(RANDOM.nextInt(players.size()));
						tenti.x = rpp.x;
						tenti.y = rpp.y;
						tenti.r = SEASON_TINT[current_phase][0];
						tenti.g = SEASON_TINT[current_phase][1];
						tenti.b = SEASON_TINT[current_phase][2];
						if(RANDOM.nextInt(5)==0) {
							tenti.mulX = tenti.mulY = 4;
							tenti.setTexture(getSet("textures/squor.png"));
						}else {
							tenti.setTexture(getSet("textures/lasr.png"));
						}
							
						world.addEntity(tenti);
					}
					if(!isPlayerAlive() && !t_isDyning) {
						t_isDyning = true;
						SoundUtils.playGameOver();
						threads.add(Thread.startVirtualThread(()->{
							try {
								long t = System.currentTimeMillis();
								float maxScoreA = 0;
								float maxScoreB = 0;
								for(Player p : players) {
									if(p.score > maxScoreA)
										maxScoreA = p.score;
									if(p.timescore > maxScoreB)
										maxScoreB = p.timescore;
								}
								config.setNumber(CONFIGPROP_HIGHSCORE_POINTS, maxScoreA);
								config.setNumber(CONFIGPROP_HIGHSCORE_TIME, maxScoreB);
								Config.save();
								while(t > System.currentTimeMillis()-2000) {
									float a = Math.max(0, 1-(System.currentTimeMillis()-t)/1000.0f);
									setGlobalTint(a, a, a, 1);
									Thread.sleep(20);
								}
							} catch (InterruptedException e) {}
							resetWorld();
						}));
					}
				}
				
			};
			dummy.visible = false;
			dummy.g = 0;
			dummy.b = 0.5f;
			dummy.currentLayer = 3;
			world.addEntityR(dummy);
			
		}
		{
			world.switchLevelNOW(WORLD_LOADING);
			world.addEntityR(new Entity(0, 0, WORLD_WIDTH, WORLD_HEIGHT).setTexture(getSet("ui/loading_audio.png")));
			var prg = new progressBar();
			prg.currentLayer = 6;
			prg.x = 0;
			prg.y = 0;
			prg.w = 8;
			prg.width = prg.w;
			prg.h = 1;
			prg.fraction = 0;
			prg.setTexture(getSet("white.png"));
			var bck = new Entity(prg.x, prg.y, prg.w, prg.h) {
				@Override
				public void update(double delta) {
					prg.fraction += delta;
				}
			};
			bck.r = 0.0f;
			bck.g = 0.1f;
			bck.b = 0.1f;
			bck.a = 1;
			bck.currentLayer = 5;
			bck.setTexture(getSet("white.png"));
			world.addEntityR(prg);
			world.addEntityR(bck);
		}
		
		if(SoundUtils.fr) {
		world.switchLevelNOW(WORLD_LOADING);
		
		threads.add(Thread.startVirtualThread(()->{
				try {
					SoundUtils.init();
					world.switchLevelNOW(WORLD_MAINMENU);
					SoundUtils.startMainMenuMusic();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
		}));
		} else {
			world.switchLevelNOW(WORLD_MAINMENU);
			SoundUtils.startMainMenuMusic();
		}
		world.setRuntime();
		
		setGlobalTint(0, 0, 0, 1);
		setGlobalRotation(0);
		threads.add(Thread.startVirtualThread(()->{
			try {
				long t2 = System.currentTimeMillis();
				while(t2 > System.currentTimeMillis()-1000) {
					float a = Math.min(1, (System.currentTimeMillis()-t2)/1000.0f);
					setGlobalTint(a, a, a, 1);
					Thread.sleep(20);
				}
			} catch (InterruptedException e) {
			}
		}));
		
		t_isDyning = false;
		t_isChangingPhase = false;
		t_isJoining = false;
		current_phase = 0;
		for(var e : secondPhaseBackgrounds)
			e.a = 0;
		secondPhaseBackgrounds[0].a = 1;
		readyInputMethods.clear();
	}
	public static DescriptorSet getSet(String name){
		if(texturesM.containsKey(name))
			return texturesM.get(name);
		AGTexture a = null;
		try {
			a = AWTIntegration.createTexture2D(graphics, loadResourceImage(name));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		DescriptorSet set = graphics.createDescriptorSets(texturePool, mainShader.getDescriptorLayouts()[0])[0];
		graphics.setDataOfDescriptorSet(set, AGVGraphics.di(a, sampler, VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER));
		textures.add(a);
		texturesM.put(name, set);
		return set;
	}
//	private static ImInt imgui_i1 = new ImInt();
	private static void renderGUI() throws Throwable {
		{ //ImGui test
//			ImGui.newFrame();
//			imguiIn.newFrame();
//			
//			ImGui.beginMainMenuBar();
//			
//			if(ImGui.menuItem("open")) {
//				
//				
//			}
//			ImGui.text("Total Buffers: " + world.getAllocatedBuffers() + ", Spare buffers: " + world.getSpareBufferSize());
//			ImGui.endMainMenuBar();
//			float[] f = new float[] {0};
//			if(ImGui.dragFloat("wm", f))
//				world.worldMoveDX = f[0];
//			if(ImGui.inputInt("phase", imgui_i1))
//				setPhase(imgui_i1.get());
			
//			for(Player p : players)
//				ImGui.text("§ " + p.poitns);
//            ImGui.endFrame();
//            ImGui.render();
//            ImGui.renderPlatformWindowsDefault();
//            ImGui.updatePlatformWindows();
//			imguiIn.updateMainBuffers();
		}
	}
	
	public static String loadResourceString(String name) throws IOException {
		var s = Main.class.getResourceAsStream("/assets/"+name);
		if(s == null)
			throw new IllegalStateException("Couldn't find file '" + name+"'! ");
		String res = new String(s.readAllBytes(), StandardCharsets.UTF_8);
		s.close();
		return res;
	}
	public static BufferedImage loadResourceImage(String name) throws IOException {
		var s = Main.class.getResourceAsStream("/assets/"+name);
		if(s == null)
			throw new IllegalStateException("Couldn't find file '" + name+"'! ");
		BufferedImage i = ImageIO.read(s);
		s.close();
		return i;
	}
	public static ByteArrayInputStream loadResourceIS(String name) throws IOException {
		var s = Main.class.getResourceAsStream("/assets/"+name);
		if(s == null)
			throw new IllegalStateException("Couldn't find file '" + name+"'! ");
		ByteArrayInputStream baos = new ByteArrayInputStream(s.readAllBytes());
		s.close();
		return baos;
	}

}
