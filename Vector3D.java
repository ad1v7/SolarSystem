/** 
 * A class for vector in 3D space, complete with constructors
 * setters and getters.
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @version "11/2015"
 *
 */

public class Vector3D {

    // Private properties
    private double xCoord;
    private double yCoord;
    private double zCoord;

    /* 
     * Constructors
     *
     */

    /** Default constructor. Constructs a new 3D Vector and sets it to (0,0,0)
     *
     */
    public Vector3D () {
	this.setVector3D(0.0,0.0,0.0);
    }

    /** Explicit constructor. Constructs a new 3D Vector
     *  from input (x,y,z)
     *
     * @param x a double to set a x-coordinate
     * @param y a double to set a y-coordinate
     * @param z a double to set a z-coordinate
     */

    public Vector3D(double x, double y, double z) {
	this.setVector3D(x, y, z);
    }

    /** Copy constructor. Constructs a new 3D Vector
     *  by copying x,y,z coordinates of another Vector3D instance
     *
     * @param original the  Vector3D to be copied
     */

    public Vector3D(Vector3D original) {
	this.setVector3D(original.getX(), original.getY(), original.getZ());
    }


    /*
     * Setters and getters
     *
     */ 

    /** Set method to set x-coordinates of a vector only
     *
     * @param x a double to set a x-coordinate
     */ 
    public void setX(double x) {
	this.xCoord = x;
    }

    /** Set method to set y-coordinates of a vector only
     *
     * @param y a double to set a y-coordinate
     */ 
    public void setY(double y) {
	this.yCoord = y;
    }

    /** Set method to set z-coordinates of a vector only
     *
     * @param z a double to set a z-coordinate
     */ 
    public void setZ(double z) {
	this.zCoord = z;
    }

    /** Set method to set all coordinates of a vector
     *
     * @param x a double to set a x-coordinate
     * @param y a double to set a y-coordinate
     * @param z a double to set a z-coordinate
     */ 
    public void setVector3D(double x, double y, double z) {
	this.setX(x);
	this.setY(y);
	this.setZ(z);
    }

    /** Get method to get x-coordinates of a vector
     *
     * @return x a double to get a x-coordinate
     */ 
    
    public double getX() {
	return this.xCoord;
    }
    
    /** Get method to get y-coordinates of a vector
     *
     * @return y a double to get a y-coordinate
     */ 
    public double getY() {
	return this.yCoord;
    }

    /** Get method to get z-coordinates of a vector
     *
     * @return z a double to get a z-coordinate
     */ 
    public double getZ() {
	return this.zCoord;
    }
    // 0.0 is added to avoid -0.0
    /** Returns a String representation of the Vector3D
     *
     * @return a string representation of the Vector3D
     */
    public String toString() {
	double x = this.getX()+0.0;
	double y = this.getY()+0.0;
	double z = this.getZ()+0.0;
	return x + y + z;
    }

    /*
     * Instance methods
     *
     */

    /** Calculates the magnitude of the Vector3D
     *
     * @return a double representing the Vector3D magnitude
     */
    public double mag() {
	return Math.sqrt(this.magSq());
    }

    /** Calculates the magnitude squared of the Vector3D
     *
     * @return a double representing the Vector3D square magnitude
     */
    public double magSq() {
	return this.getX() * this.getX() +
	    this.getY() * this.getY() +
	    this.getZ() * this.getZ();
    }

    /** Scalar multiplication  of a double and the Vector3D
     *
     * @param a the scalar to by multiplied
     * @return a Vector3D representing scalar multiplication of double and Vector3D
     */
    public Vector3D scalMul(double a) {
	return new Vector3D(a*this.getX(), a*this.getY(), a*this.getZ());
    }

    /** Scalar division  of the Vector3D and a double
     *
     * @param a scalar to be used for division
     * @return a Vector3D representing division the Vector3D and a double
     */
    public Vector3D scalDiv(double a) {
	return new Vector3D(this.getX()/a, this.getY()/a, this.getZ()/a);
    }

    /*
     * Static methods
     *
     */

    /** Vector addition of two Vector3D vectors
     *
     * @param a the first Vector3D to be added
     * @param b the second Vector3D to be added
     * @return the sum of a and b, a+b
     */
    public static Vector3D vecAdd(Vector3D a, Vector3D b) {
	return new Vector3D(a.getX() + b.getX(),
			    a.getY() + b.getY(),
			    a.getZ() + b.getZ());
    }

    /** Vector subtraction of two Vector3D vectors
     *
     * @param a the subtrahend
     * @param b the subtractor
     * @return the difference between a and b, a-b
     */
    public static Vector3D vecSub(Vector3D a, Vector3D b) {
	return new Vector3D(a.getX() - b.getX(),
			    a.getY() - b.getY(),
			    a.getZ() - b.getZ());
    }

    /** Cross product of two Vector3D vectors
     *
     * @param a the Vector3D is on the LHS of x  
     * @param b the Vector3D is on the RHS of x
     * @return the cross product of a and b, axb
     */
    public static Vector3D vecCross(Vector3D a, Vector3D b) {
	return new Vector3D((a.getY() * b.getZ() - a.getZ() * b.getY()), 
			    -(a.getX() * b.getZ() - a.getZ() * b.getX()),  
			    (a.getX() * b.getY() - a.getY() * b.getX()));
    }

    /** Dot product of two Vector3D vectors
     *
     * @param a the first Vector3D
     * @param b the second Vector3D
     * @return the dot product of a and b, a dot b
     */ 
    public static double vecDot(Vector3D a, Vector3D b) {
	return (a.getX() * b.getX()+
		a.getY() * b.getY()+
		a.getZ() * b.getZ());

    }
    
}
