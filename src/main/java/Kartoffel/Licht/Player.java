package Kartoffel.Licht;

public class Player extends Entity{
	
	InputMethod method;
	String name;
	float poitns = 100;
	float score = 0;
	float timescore = 0;
	long lastDash = 0;
	float speedBoost = 0;
	PlatformSpawner p;
	double airTime = 0;
	
	public static float PLAYER_SPEED = 10;
	public static float SPEED_BOOST_PER_SEC = 50;
	public static int DASH_COOLDOWN = 800; //MS
	public static double MAX_AIR_TIME = 1;
	
	public static float PLAYER_WIDTH = 0.6f;
	public static float PLAYER_HEIGHT = 0.6f;
	public static float MAX_POINTS = 100.0f;
	
	public static int ABB_COOLDOWN = 15; //s
	public static int ABC_COOLDOWN = 10; //s
	public static int ABD_COOLDOWN = 30; //s
	
	public static int ABB_DURATION = 5000; //MS
	public static int ABB_COST = 35;
	
	public static int ABC_DURATION = 3; //s
	public static int ABC_COST = 25;
	
//	public static int ABD_DURATION = 5; //s
	public static int ABD_COST = 45;
	
	public static boolean ABB_DISABLED = false;
	public static boolean ABC_DISABLED = false;
	public static boolean ABD_DISABLED = false;
	
	boolean t_a1 = false;
	boolean t_a2 = true;
	float prot = 0;
	double t_anim1 = -1;
	double t_anim2 = 0;
	double t_animWALK = 0;
	double t_animhurt = 0;
	int st_anim = 0;
	
	double timeFloating = 0;
	
	double time = 0;
	
	public int skin = 0;
	
	public double cooldown_AB_B = 0;
	public double cooldown_AB_C = 0;
	public double cooldown_AB_D = 0;
	
	public Player(InputMethod method, PlatformSpawner spawnr, int skin) {
		this.method = method;
		w = 0.6f;
		h = 0.6f;
		this.real = true;
		this.despawnable = false;
		this.p = spawnr;
		this.currentLayer = 3;
		poitns = MAX_POINTS;
		this.skin = skin;
		this.texture = Main.getSet("textures/entite.png");
		this.tw = -PlayerListDisplay.width;
		this.th = PlayerListDisplay.height;
		this.ty = PlayerListDisplay.height*(1+skin);
		this.tx = PlayerListDisplay.width;
	}
	
	@Override
	public void update(double delta) {
		time += delta;
		if(isDead()) {
			ty = 0;
			float k = (float) Math.min(Math.max(t_anim1/2, 0), 1);
			a = 0.25f*k;
			w = PLAYER_WIDTH*k;
			h = PLAYER_HEIGHT*k;
			float dx = method.getXOff();
			float dy = method.getYOff();
			float speed = (float) Math.sqrt(dx*dx+dy*dy);
			if((dx != 0 || dy != 0) && (t_anim1 >= 2)) {
				double dddx = dx*delta*PLAYER_SPEED/speed*0.5;
				double dddy = dy*delta*PLAYER_SPEED/speed*0.5;
				double ddx = dddx*Main.GLOBAL_ROTATIONcs-dddy*Main.GLOBAL_ROTATIONsn;
				double ddy = dddy*Main.GLOBAL_ROTATIONcs+dddx*Main.GLOBAL_ROTATIONsn;
				t_anim2 += delta*2;
				rot += Math.cos(t_anim2)*delta*2;
				x += ddx;
				if(isCollidingWall(0)) {
					x *= Math.min(Main.WORLD_WIDTH/Math.abs(x), 1);
				}
				y -= ddy;
				if(isCollidingWall(0)) {
					y *= Math.min(Main.WORLD_HEIGHT/Math.abs(y), 1);
				}
				changed = true;
				tx = Main.RANDOM.nextInt(4)*PlayerListDisplay.width;
			}
			if(t_a2 || t_anim1 < 2) {
				t_a2 = false;
				changed = true;
				t_anim1 += delta;
				rot += delta/t_anim1;
				if(t_anim1 < 1)
					tx = PlayerListDisplay.width*5;
				else if(t_anim1 < 2)
					tx = PlayerListDisplay.width*4;
			}
				
			return;
		}
		int ANIMATION = 0;
		timescore += delta;
		boolean pl = p.isCollidingPlatform(this, 0);
		boolean wll = isCollidingWall(0);
		float dxx = method.getXOff();
		float dyy = method.getYOff();
		double dx = dxx*Main.GLOBAL_ROTATIONcs-dyy*Main.GLOBAL_ROTATIONsn;
		double dy = dyy*Main.GLOBAL_ROTATIONcs+dxx*Main.GLOBAL_ROTATIONsn;
		float speed = (float) Math.sqrt(dx*dx+dy*dy);
		boolean movingf = dx != 0 || dy != 0;
		if(movingf) {
			t_animWALK += delta;
			ANIMATION = (int) (t_animWALK*10)%2;
			prot = (float) -Math.atan2(dy, dx);
			if(Math.abs(prot-rot) > Math.PI) {
				if(prot <= 0)
					prot += Math.PI*2;
				else
					prot -= Math.PI*2;
			}
			if(speedBoost > 0) {
				ANIMATION = 2;
				float ta = (float) (Math.min(SPEED_BOOST_PER_SEC*delta, speedBoost));
				speedBoost -= ta;
				x += dx*ta;
				y -= dy*ta;
				if(!t_a1 && !pl)
					t_a1 = true;
				if(t_a1 && pl) {
					speedBoost*=0.0;
					t_a1 = false;
				}
			}
			x += dx*delta*PLAYER_SPEED/speed;
			y -= dy*delta*PLAYER_SPEED/speed;
			changed = true;
		}
		if(Math.abs(prot-rot)>0.01) {
			rot = (float) (prot*.1+rot*.9);
			changed = true;
		}
		if(t_animhurt > 0) {
			t_animhurt -= delta;
			ANIMATION = 3;
		}
		if(method.isButtonAPressed() && lastDash < System.currentTimeMillis()-DASH_COOLDOWN && movingf) {
			speedBoost = 5;
			lastDash = System.currentTimeMillis();
			t_a1 = false;
		}
		if((!pl || wll) && (timeFloating <= 0)) {
			if(wll)
				airTime += delta;
			airTime += delta;
			float s = (float) (1-Math.pow(airTime/MAX_AIR_TIME, 2));
			w = PLAYER_WIDTH*s;
			h = PLAYER_HEIGHT*s;
			a = s;
			changed = true;
		} else {
			airTime = 0;
			w = PLAYER_WIDTH;
			h = PLAYER_HEIGHT;
			a = 1;
			changed = true;
		}
		if(airTime > MAX_AIR_TIME) {
			poitns = 0;
			t_anim1 = -1;
		}
		if(timeFloating >= 0)
			timeFloating -= delta;
		poitns-=delta;
		if(st_anim != ANIMATION) {
			changed = true;
			this.tx = PlayerListDisplay.width*ANIMATION+PlayerListDisplay.width;
			st_anim = ANIMATION;
		}
		if(cooldown_AB_B > 0) { //Time bomb
			if(cooldown_AB_B == ABB_COOLDOWN) {
//				System.out.println("Used ability time bomb");
				Main.ABILITY_TIMEFRZ_TSTMP = System.currentTimeMillis()+ABB_DURATION;
				getAbilityDamage(ABB_COST);
				SoundUtils.playTiktok();
			}
			cooldown_AB_B -= delta;
			
		} else 	if(method.isButtonBPressed() && !ABB_DISABLED) {
			cooldown_AB_B = ABB_COOLDOWN;
		}
		if(cooldown_AB_C > 0) { //Balloon
			if(cooldown_AB_C == ABC_COOLDOWN) {
//				System.out.println("Used ability balloon");
				getAbilityDamage(ABC_COST);
				timeFloating = ABC_DURATION;
				SoundUtils.playBallon();
			}
			cooldown_AB_C -= delta;
			
		} else 	if(method.isButtonCPressed() && !ABC_DISABLED) {
			cooldown_AB_C = ABC_COOLDOWN;
		}
		if(cooldown_AB_D > 0) { //Plattform pouch
			if(cooldown_AB_D == ABD_COOLDOWN) {
//				System.out.println("Used ability plattform");
				getAbilityDamage(ABD_COST);
				p.addBigPlatform();
				SoundUtils.playPlattform();
			}
			cooldown_AB_D -= delta;
			
		} else 	if(method.isButtonDPressed() && !ABD_DISABLED) {
			cooldown_AB_D = ABD_COOLDOWN;
		}
	}
	public boolean underDashCooldown() {
		return lastDash >= System.currentTimeMillis()-DASH_COOLDOWN;
	}
	public boolean underAbilityBCooldown() { //Time bomb
			return cooldown_AB_B > 0 || ABB_DISABLED;
	}
	public boolean underAbilityCCooldown() { //Balloon
		return cooldown_AB_C > 0 || ABC_DISABLED;
	}
	public boolean underAbilityDCooldown() { //Plattform pouch
		return cooldown_AB_D > 0 || ABD_DISABLED;
	}

	
	public void acceptPower(float p) {
//		boolean crack = poitns+p >= MAX_POINTS; //TODO Play Soundeffect
		SoundUtils.playEatSound();
		score += p;
		poitns = Math.min(p+poitns, MAX_POINTS);
	}
	
	public void damage(float amount) {
		poitns = Math.max(poitns-amount, 0);
		SoundUtils.playHitSound();
		t_animhurt = 0.5f;
	}
	public void getAbilityDamage(float amount) {
		poitns = Math.max(poitns-amount, 0);
		SoundUtils.playHitSound();
	}
	
	public boolean isDead() {
		return poitns <= 0;
	}
	
}
