package Kartoffel.Licht;

public class CollisionTools {
	
//	static boolean contained(float min1, float max1, float min2, float max2) {
//		return Math.max(min1, min2) < Math.min(max1, max2);
//	}
	
//	public static boolean isCollidingRotated_H(Entity b, Entity a) {
//		float v1_dx = (float) Math.cos(a.rot); //Direction 1
//		float v1_dy = (float) Math.sin(a.rot); 
//		float v2_dx = v1_dx; //Direction 2
//		float v2_dy = -v1_dy;
//		float v3_dx = (float) Math.cos(b.rot); //Direction 3
//		float v3_dy = (float) Math.sin(b.rot); 
//		float v4_dx = v1_dx; //Direction 4
//		float v4_dy = -v1_dy;
//		float p1x1 = 0;
//		float p1y1 = 0;
//		float p2x1 = 0;
//		float p2y1 = 0;
//		float p3x1 = 0;
//		float p3y1 = 0;
//		float p4x1 = 0;
//		float p4y1 = 0;
//		{
//			float sn = (float) Math.sin(a.rot);
//			float cs = org.joml.Math.cosFromSin(sn, a.rot);
//			float w = (float) (a.w);
//			float h = (float) (a.h);
//			float w1 = w;
//			float h1 = h;
//			float w2 = w;
//			float h2 = -h;
//			float w3 = -w;
//			float h3 = -h;
//			float w4 = -w;
//			float h4 = h;
//			p1x1 = a.x+w1*cs-h1*sn;
//			p1y1 = a.y+h1*cs+w1*sn;
//			p2x1 = a.x+w2*cs-h2*sn;
//			p2y1 = a.y+h2*cs+w2*sn;
//			p3x1 = a.x+w3*cs-h3*sn;
//			p3y1 = a.y+h3*cs+w3*sn;
//			p4x1 = a.x+w4*cs-h4*sn;
//			p4y1 = a.y+h4*cs+w4*sn;
//		}
//		float p1x2 = 0;
//		float p1y2 = 0;
//		float p2x2 = 0;
//		float p2y2 = 0;
//		float p3x2 = 0;
//		float p3y2 = 0;
//		float p4x2 = 0;
//		float p4y2 = 0;
//		{
//			float sn2 = (float) Math.sin(a.rot);
//			float cs2 = org.joml.Math.cosFromSin(sn2, a.rot);
//			float w2 = (float) (a.w);
//			float h2 = (float) (a.h);
//			float w12 = w2;
//			float h12 = h2;
//			float w22 = w2;
//			float h22 = -h2;
//			float w32 = -w2;
//			float h32 = -h2;
//			float w42 = -w2;
//			float h42 = h2;
//			p1x2 = a.x+w12*cs2-h12*sn2;
//			p1y2 = a.y+h12*cs2+w12*sn2;
//			p2x2 = a.x+w22*cs2-h22*sn2;
//			p2y2 = a.y+h22*cs2+w22*sn2;
//			p3x2 = a.x+w32*cs2-h32*sn2;
//			p3y2 = a.y+h32*cs2+w32*sn2;
//			p4x2 = a.x+w42*cs2-h42*sn2;
//			p4y2 = a.y+h42*cs2+w42*sn2;
//		}
//		float v1_p1_dot1 = v1_dx*p1x1+v1_dy*p1y1; //Shape A with rot of shape A
//		float v1_p2_dot1 = v1_dx*p2x1+v1_dy*p2y1;
//		float v1_p3_dot1 = v1_dx*p3x1+v1_dy*p3y1;
//		float v1_p4_dot1 = v1_dx*p4x1+v1_dy*p4y1;
//		float v2_p1_dot1 = v2_dx*p1x1+v2_dy*p1y1;
//		float v2_p2_dot1 = v2_dx*p2x1+v2_dy*p2y1;
//		float v2_p3_dot1 = v2_dx*p3x1+v2_dy*p3y1;
//		float v2_p4_dot1 = v2_dx*p4x1+v2_dy*p4y1;
//		System.out.println("a " + v2_p2_dot1);
//		float v1_p1_dot2 = v1_dx*p1x2+v1_dy*p1y2; //Shape B with rot of shape A
//		float v1_p2_dot2 = v1_dx*p2x2+v1_dy*p2y2;
//		float v1_p3_dot2 = v1_dx*p3x2+v1_dy*p3y2;
//		float v1_p4_dot2 = v1_dx*p4x2+v1_dy*p4y2;
//		float v2_p1_dot2 = v2_dx*p1x2+v2_dy*p1y2;
//		float v2_p2_dot2 = v2_dx*p2x2+v2_dy*p2y2;
//		float v2_p3_dot2 = v2_dx*p3x2+v2_dy*p3y2;
//		float v2_p4_dot2 = v2_dx*p4x2+v2_dy*p4y2;
//		
//		float v1_max1 = Math.max(Math.max(v1_p1_dot1, v1_p2_dot1), Math.max(v1_p3_dot1, v1_p4_dot1));
//		float v1_min1 = Math.min(Math.min(v1_p1_dot1, v1_p2_dot1), Math.min(v1_p3_dot1, v1_p4_dot1));
//		float v2_max1 = Math.max(Math.max(v2_p1_dot1, v2_p2_dot1), Math.max(v2_p3_dot1, v2_p4_dot1));
//		float v2_min1 = Math.min(Math.min(v2_p1_dot1, v2_p2_dot1), Math.min(v2_p3_dot1, v2_p4_dot1));
//		float v1_max2 = Math.max(Math.max(v1_p1_dot2, v1_p2_dot2), Math.max(v1_p3_dot2, v1_p4_dot2));
//		float v1_min2 = Math.min(Math.min(v1_p1_dot2, v1_p2_dot2), Math.min(v1_p3_dot2, v1_p4_dot2));
//		float v2_max2 = Math.max(Math.max(v2_p1_dot2, v2_p2_dot2), Math.max(v2_p3_dot2, v2_p4_dot2));
//		float v2_min2 = Math.min(Math.min(v2_p1_dot2, v2_p2_dot2), Math.min(v2_p3_dot2, v2_p4_dot2));
//		boolean collisionA = contained(v1_min1, v1_max1, v1_min2, v1_max2);
//		boolean collisionB = contained(v2_min1, v2_max1, v2_min2, v2_max2);
//		return collisionA&&collisionB; //Thencollision
//	}
	
	
    // Get rotated corners of a rectangle
	static float[][] getCorners(Entity e) {
        float sn = (float) Math.sin(e.rot);
        float cs = org.joml.Math.cosFromSin(sn, e.rot);
        float w = e.w;
        float h = e.h;

        float[] x = new float[4];
        float[] y = new float[4];

        // Top-right
        x[0] = e.x +  w * cs -  h * sn;
        y[0] = e.y +  h * cs +  w * sn;

        // Bottom-right
        x[1] = e.x +  w * cs - (-h) * sn;
        y[1] = e.y + (-h) * cs +  w * sn;

        // Bottom-left
        x[2] = e.x + (-w) * cs - (-h) * sn;
        y[2] = e.y + (-h) * cs + (-w) * sn;

        // Top-left
        x[3] = e.x + (-w) * cs -  h * sn;
        y[3] = e.y +  h * cs + (-w) * sn;

        return new float[][] { x, y };
    }
	
    // Project a rectangle onto an axis
    static float[] projectOntoAxis(float[] px, float[] py, float ax, float ay) {
        float min = ax * px[0] + ay * py[0];
        float max = min;
        for (int i = 1; i < 4; i++) {
            float dot = ax * px[i] + ay * py[i];
            if (dot < min) min = dot;
            if (dot > max) max = dot;
        }
        return new float[] { min, max };
    }
    
    // Check if projections overlap
    static boolean projectionsOverlap(float min1, float max1, float min2, float max2) {
        return max1 >= min2 && max2 >= min1;
    }
    
	public static boolean isCollidingRotated_H(Entity a, Entity b) {

	    // Get corners of both rectangles
	    float[][] aCorners = getCorners(a);
	    float[][] bCorners = getCorners(b);
	    float[] ax = aCorners[0], ay = aCorners[1];
	    float[] bx = bCorners[0], by = bCorners[1];

	    // Define 4 axes: 2 from A, 2 from B
	    float[][] axes = new float[4][2];
	    axes[0][0] = ax[1] - ax[0]; axes[0][1] = ay[1] - ay[0]; // A edge 1
	    axes[1][0] = ax[3] - ax[0]; axes[1][1] = ay[3] - ay[0]; // A edge 2
	    axes[2][0] = bx[1] - bx[0]; axes[2][1] = by[1] - by[0]; // B edge 1
	    axes[3][0] = bx[3] - bx[0]; axes[3][1] = by[3] - by[0]; // B edge 2

	    // Check SAT on all 4 axes
	    for (int i = 0; i < 4; i++) {
	        float axDir = axes[i][0];
	        float ayDir = axes[i][1];
	        float length = (float) Math.sqrt(axDir * axDir + ayDir * ayDir);
	        if (length == 0) continue; // Avoid division by zero
	        float axNorm = axDir / length;
	        float ayNorm = ayDir / length;

	        float[] projA = projectOntoAxis(ax, ay, axNorm, ayNorm);
	        float[] projB = projectOntoAxis(bx, by, axNorm, ayNorm);

	        if (!projectionsOverlap(projA[0], projA[1], projB[0], projB[1])) {
	            return false; // Found separating axis
	        }
	    }

	    return true; // No separating axis, collision exists
	}

}
