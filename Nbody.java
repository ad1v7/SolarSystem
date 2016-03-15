/**
 * Computer Modelling, Project
 * Simulation of the Solar System
 *
 * Run with ARGUMENTS: particle_file parameter_file output.xyz
 * Units: AU, days, Solar masses
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @version "03/2016"
 *
 */

import java.io.*;
import java.util.Scanner;
import java.lang.*;

public class Nbody {

    public static double G;
    public static double YEAR;

    public static void main (String[] argv) throws IOException {

        if (argv.length != 3) {
            System.err.println("Usage: java Nbody <particle_file> <parameter_file> <output_file>");
            System.exit(-1);
        }

	// Open the input file containing particles
	String particleFile = argv[0];
	String expData = "expData.dat";
	// Count number of particles in an input file
	BufferedReader particleBuff = new BufferedReader(new FileReader(particleFile));
	int numberOfParticles = 0;
	while (particleBuff.readLine() != null) {
	    numberOfParticles++;
	}
	particleBuff.close();

	// Comparision with exp data
	BufferedReader expDataBuff = new BufferedReader(new FileReader(expData));
	Scanner scanExpData = new Scanner(expDataBuff);
	double[] expOrbit = new double[numberOfParticles];
	double[] expAphelion = new double[numberOfParticles];
	double[] expPerihelion = new double[numberOfParticles];
	for (int i=0; scanExpData.hasNext(); i++) {

	    expOrbit[i] =scanExpData.nextDouble();
	    expAphelion[i] =scanExpData.nextDouble();
	    expPerihelion[i] =scanExpData.nextDouble();

	}


	// Attach a scanner to the input file
	particleBuff = new BufferedReader(new FileReader(particleFile));
	Scanner scanParticles = new Scanner(particleBuff);

	// Create new array of Particles3D and copy particles from an input file
	// Find array index of Earth; used later for Moon orbit calculations
	int earthIndex =0;
	Particle3D particleArray[] = new Particle3D[numberOfParticles];
	for (int i=0; scanParticles.hasNext(); i++) {
	    particleArray[i] = Particle3D.pScanner(scanParticles);
	    if (particleArray[i].getLabel().equals("Earth")) {
		earthIndex=i;
	    }
	}
	scanParticles.close();	

	/*
	 *  Adjust velocities of all Particles so the CoM = 0
	 */

	// Calc total linear momentum and mass of the system
	System.out.printf("\nUncorrected Total Linear Momentum: %1.2e M☉ AU/day\n", totLinMom(particleArray).mag());
	double systemMass = 0.0;	
	for (int i=0; i<numberOfParticles; i++) {
	    systemMass += particleArray[i].getMass();
	}

	// Calculate Centre of Mass velocity
	Vector3D centreOfMass = new Vector3D(totLinMom(particleArray).scalDiv(systemMass));

	// Correct particle velocities by CoM velocity
	for (int i=0; i<numberOfParticles; i++) {
	    particleArray[i].setVelocity(Vector3D.vecSub(particleArray[i].getVelocity(), centreOfMass));
	}

	System.out.printf("Total Mass of The System: %1.4f M☉\n", systemMass);

	// Recalculate total linear momentum and print
	System.out.printf("Corrected Total Linear Momentum: %1.2e M☉ AU/day\n", totLinMom(particleArray).mag());

	/* 
	 * End of CoM correction
	 */

	// Open the second input file containing parameters: number of steps, size of timestep,
	// initial time, print frequency, value of k, length of year in days
	String paramFile = argv[1];

	// Attach a scanner to the parameter input file
	BufferedReader paramBuff = new BufferedReader(new FileReader(paramFile));
	Scanner scanParam = new Scanner(paramBuff);
	double numberOfSteps = scanParam.nextDouble();
	double stepSize = scanParam.nextDouble();
	double time = scanParam.nextDouble();
	int printFrequency = scanParam.nextInt();
	G = Math.pow(scanParam.nextDouble(),2);
	YEAR = scanParam.nextDouble();
	scanParam.close();

	// Opens the output file
	String outFile = argv[2];
        PrintWriter output = new PrintWriter(new FileWriter(outFile));

	// Set up arrays which store forces
	Vector3D currentForceArray[] = new Vector3D[numberOfParticles];
	Vector3D newForceArray[] = new Vector3D[numberOfParticles];

	// Set all forces to 0 to avoid later problems
	for (int i = 0; i < numberOfParticles; i++ ) {
	    currentForceArray[i] = new Vector3D();
	    newForceArray[i] = new Vector3D();
	}

	// Calculate initial forces
	Particle3D.updateForce(particleArray, currentForceArray);
	
	// Prints the initial positions to the output file
	int stepNumber = 1;
	output.printf(Particle3D.vmd(particleArray, stepNumber));
	stepNumber++;

	// Arrays for orbit counter
	double newAngle[] = new double[numberOfParticles];
	double prevAngle[] = new double[numberOfParticles];
	double angleDiff[] = new double[numberOfParticles]; 
	boolean clockwise[] = new boolean[numberOfParticles];

	// doubles for energy fluctuation calculation
	double minEnergy = Particle3D.sysEnergy(particleArray);
	double maxEnergy = Particle3D.sysEnergy(particleArray);
	double energy;

	// Arrays of doubles to store values of aphelion and perihelion for each body
	double aphelionArray[] = new double[numberOfParticles];
	double perihelionArray[] = new double[numberOfParticles];

	/*
	 * Determine conditions at t = 0
	 */

	// Determine clockwise/anitclockwise orbits for each body
	for (int j=0; j<numberOfParticles; j++) {
	    if (Vector3D.vecCross(particleArray[j].getPosition(),particleArray[j].getVelocity()).getZ() > 0) {
		clockwise[j] = false;
	    }
	    else { clockwise[j] = true; }
	}

	// Calc initial angles before the position update for orbit calculation
	angles(earthIndex,  particleArray, prevAngle);

	/* End of initial conditions */

	/* 
	 * Loop over times steps
	 */

	System.out.print("Percent completed:   0%");
	for (int i=0; i<numberOfSteps; i++) {

	    /*
	     * Start of the Verlet Algorithm
	     */

	    // Update the position using current velocity
	    Particle3D.leapPosition(stepSize, particleArray, currentForceArray);

	    // Update force after time leap
	    Particle3D.updateForce(particleArray, newForceArray);
  
	    // Update the velocity ready for the next position update
	    Particle3D.leapVelocity(stepSize, particleArray, currentForceArray, newForceArray);

	    // Update force
	    for (int j=0; j < numberOfParticles; j++) {
		currentForceArray[j] = new Vector3D(newForceArray[j]);
		newForceArray[j] = new Vector3D();
	    }

	    // Increase the time
	    time = time + stepSize;

	    /*
	     *  end of the Verlet Algorithm
	     */

	    // Calculate aphelion and perihelion for each body in the simulation
	    helion(particleArray, earthIndex, aphelionArray, perihelionArray);

	    // Prints every k-th position to VMD file
	    if (i % printFrequency == 0) {	   
		output.printf(Particle3D.vmd(particleArray, stepNumber));

		// Calc min and max energy every k-th step to save on calc
		energy = Particle3D.sysEnergy(particleArray);
		if (minEnergy > energy) { minEnergy = energy;  }
		else if (maxEnergy < energy) { maxEnergy = energy;  }	

		// Print % progress
		System.out.printf("\b\b\b\b\b %3.0f%%", (i)/numberOfSteps*100 );
	    }

	    // Orbit Counter
	    angles(earthIndex,  particleArray, newAngle);
	    orbitCounter(particleArray,  prevAngle, newAngle,  clockwise, angleDiff);

	    stepNumber++;
	}

	/*
	 * End of loop over timesteps
	 */

	double[] orbitTimeInDays = new double[numberOfParticles];
	double[] numberOfOrbits = new double[numberOfParticles];

	// Calc orbitTimeInDays and numberOfOrbits for each body
	for (int i=0; i<numberOfParticles; i++) {
	    orbitTimeInDays[i] = time/(angleDiff[i]/(2*Math.PI));
	    numberOfOrbits[i] = angleDiff[i]/(2*Math.PI);
	}

	// Console output
	System.out.printf("\nTotal run time: %.1f days which is %.2f years.", time, time/YEAR);

	System.out.printf("\nEnergy fluctuation: %1.2e%s of the total system energy.\n",
			  -(maxEnergy-minEnergy)/((minEnergy+maxEnergy)/2)*100, "%");

	System.out.format("\n%10s%11s%13s%14s%15s%15s\n",
			  "Body Name", "Mass/M☉", "Orbit/days", "Aphelion/AU", "Perihelion/AU", "Orbit/⊕ ratio");

	for(int i=1; i<numberOfParticles; i++) {
	    System.out.format("%10s%11.2e%13.3f%14.6f%15.6f%15.4f\n",
			      particleArray[i].getLabel(),particleArray[i].getMass(), orbitTimeInDays[i],
			      aphelionArray[i], perihelionArray[i], orbitTimeInDays[i]/YEAR);
	}

	System.out.println("\n Kepler's 3rd Law verification:               Comparision with experimental data");
	System.out.format("%10s%11s%11s%12s%11s%12s%13s\n",
			  "Body Name", "T^2", "a^3", "total", "orbits", "aphelion", "perihelion");
	System.out.format("%10s%11s%11s%12s%11s%12s%13s",
			  "", "", "", "orbits", "%", "%", "%");
	for (int i=1; i<numberOfParticles; i++) {

	    System.out.printf("\n%10s%11.2e%11.2e%12.1f%11.3f%12.3f%13.3f", particleArray[i].getLabel(),
			      Math.pow(orbitTimeInDays[i]/YEAR,2), Math.pow((perihelionArray[i]+aphelionArray[i])/2,3), numberOfOrbits[i],
			      100*orbitTimeInDays[i]/expOrbit[i], 100*aphelionArray[i]/expAphelion[i], 100*perihelionArray[i]/expPerihelion[i]);
	}
	System.out.println("\n");
	// Close the output file
	output.close();
    }

    /*
     * Static Methods
     */

    // Angle between position vector and x axis
    static void angles(int earthIndex, Particle3D[] particleArray,  double[] newAngle) {
	for (int j=1; j<particleArray.length; j++) {

	    if (particleArray[j].getLabel().equals("Moon")) {
		newAngle[j] = Math.atan2(particleArray[earthIndex].getPosition().getY() - particleArray[j].getPosition().getY(),
					 particleArray[earthIndex].getPosition().getX() - particleArray[j].getPosition().getX());
	    }
	    else { newAngle[j] = Math.atan2(particleArray[j].getPosition().getY(), particleArray[j].getPosition().getX()); }
	    
	}
    }

    // Orbit Counter method returns angular displacement at time t
    static void orbitCounter(Particle3D[] particleArray, double[] prevAngle, double[] newAngle, boolean[] clockwise, double[] angleDiff) {

	for (int j=1; j<particleArray.length; j++) {

	    // clockwise case
	    if (clockwise[j] == true) {
		if (Math.signum(prevAngle[j]) > Math.signum(newAngle[j])) {
		    angleDiff[j] += (Math.abs(newAngle[j]) + prevAngle[j]);
		}
		else if (Math.signum(prevAngle[j]) < Math.signum(newAngle[j])) {
		    angleDiff[j] += (2*Math.PI -(newAngle[j] - prevAngle[j]));
		}
		else {
		    angleDiff[j] += Math.abs(newAngle[j]-prevAngle[j]);
		}
	    }

	    // anticlockwise case
	    else {		  
		if (Math.signum(prevAngle[j]) < Math.signum(newAngle[j])) {
		    angleDiff[j] += (newAngle[j] + Math.abs(prevAngle[j]));
		}
		else if (Math.signum(prevAngle[j]) > Math.signum(newAngle[j])) {
		    angleDiff[j] += (2*Math.PI + (newAngle[j] - prevAngle[j]));
		}
		else {
		    angleDiff[j] += Math.abs(newAngle[j]-prevAngle[j]);
		}
	    }
	    prevAngle[j] = newAngle[j];
	}
    }
    // method to calculate total linear momentum
    static Vector3D totLinMom(Particle3D[] particleArray) { 
	Vector3D totLinMom = new Vector3D();
	for (int i=0; i<particleArray.length; i++) {
	    totLinMom = Vector3D.vecAdd(totLinMom, particleArray[i].getVelocity().scalMul(particleArray[i].getMass()));
	}
	return totLinMom;
    } 

    // method to calculate aphelion and perihelion
    static void helion(Particle3D[] particleArray, int earthIndex, double[] aphelionArray, double[] perihelionArray) {
	double separation;
	for (int j=1; j < particleArray.length; j++) {
	    if (particleArray[j].getLabel().equals("Moon")) {
		separation = Particle3D.pSep(particleArray[earthIndex], particleArray[j]).mag();
	    }
	    else {
		separation = Particle3D.pSep(particleArray[0], particleArray[j]).mag();
	    }
	    if (aphelionArray[j] < separation) { aphelionArray[j] = separation; }
	    else if (perihelionArray[j] > separation || perihelionArray[j] == 0) { perihelionArray[j] = separation; }
	}
    }
}
