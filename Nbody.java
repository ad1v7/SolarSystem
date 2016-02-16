/**
 * Computer Modelling, Project
 * Simulation of the Solar System
 *
 * Run with ARGUMENTS: particle_file parameters output.xyz
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @version "02/2016"
 *
 */

import java.io.*;
import java.util.Scanner;
import java.lang.*;

public class Nbody {

    public static void main (String[] argv) throws IOException {

	// Open the input file containing particles
	String particleFile = argv[0];

	// Count number of particles in an input file
	BufferedReader particleBuff = new BufferedReader(new FileReader(particleFile));
	int numberOfParticles = 0;
	while (particleBuff.readLine() != null) {
	    numberOfParticles++;
	}
	particleBuff.close();

	// Attach a scanner to the input file
	particleBuff = new BufferedReader(new FileReader(particleFile));
	Scanner scanParticles = new Scanner(particleBuff);

	// Create new array of Particles3D and copy particles from an input file
	Particle3D particleArray[] = new Particle3D[numberOfParticles];
	for (int i=0; scanParticles.hasNext(); i++) {
	    particleArray[i] = Particle3D.pScanner(scanParticles);
	}
	scanParticles.close();	

	/*
	 *  Adjust velocities of all Particles so the CoM = 0
	 */

	// Calc total linear momentum and mass of the system
	double systemMass = 0.0;
	Vector3D totLinMom = new Vector3D();

	for (int i=0; i<numberOfParticles; i++) {
	    totLinMom = Vector3D.vecAdd(totLinMom, particleArray[i].getVelocity().scalMul(particleArray[i].getMass()));
	    systemMass += particleArray[i].getMass();
	}
	System.out.printf("\nUncorrected total linear momentum is: %s\n", totLinMom);

	// Calculate Centre of Mass velocity
	Vector3D centreOfMass = new Vector3D(totLinMom.scalDiv(systemMass));
	System.out.printf("\nCentre of mass velocity is: %s\n", centreOfMass);

	// Correct particle velocities by CoM velocity
	for (int i=0; i<numberOfParticles; i++) {
	    particleArray[i].setVelocity(Vector3D.vecSub(particleArray[i].getVelocity(), centreOfMass));
	}

	// Recalculate total linear momentum and print
	totLinMom = new Vector3D();
	for (int i=0; i<numberOfParticles; i++) {
	    totLinMom = Vector3D.vecAdd(totLinMom, particleArray[i].getVelocity().scalMul(particleArray[i].getMass()));
	    System.out.printf("Corrected Velocities: %s\n", particleArray[i].getVelocity());
	}
	System.out.printf("\nCorrected total linear momentum is: %s\n", totLinMom);
	System.out.printf("\nTotal mass of the system is: %1.10e\n", systemMass);


	// Open the second input file containing parameters: number of steps, size of timestep,
	// initial time, print frequency
	String paramFile = argv[1];

	// Attach a scanner to the parameter input file
	BufferedReader paramBuff = new BufferedReader(new FileReader(paramFile));
	Scanner scanParam = new Scanner(paramBuff);
	double numberOfSteps = scanParam.nextDouble();
	double stepSize = scanParam.nextDouble();
	double time = scanParam.nextDouble();
	int printFrequency = scanParam.nextInt();
	scanParam.close();

	// Opens the output file
	String outFile = argv[2];
        PrintWriter output = new PrintWriter(new FileWriter(outFile));
		
	// Set up arrays which store forces
	Vector3D currentForceArray[] = new Vector3D[numberOfParticles];
	Vector3D newForceArray[] = new Vector3D[numberOfParticles];
	
	// set all forces to 0 to avoid later problems
	for (int i = 0; i < numberOfParticles; i++ ) {
	    currentForceArray[i] = new Vector3D();
	    newForceArray[i] = new Vector3D();
	}
	
	// calculate initial forces
	Particle3D.updateForce(particleArray, currentForceArray);
		
	//Prints the initial positions to the output file
	int stepNumber = 1;
	output.printf(Particle3D.vmd(particleArray, stepNumber));
	stepNumber++;

	// arrays for orbit counter
	double newAngle[] = new double[numberOfParticles];
	double prevAngle[] = new double[numberOfParticles];
	double angleDiff[] = new double[numberOfParticles]; 

	// doubles for energy fluctuations calculations
	double minEnergy = Particle3D.sysEnergy(particleArray);
	double maxEnergy = Particle3D.sysEnergy(particleArray);
	double energy;

	// Arrays of doubles to store values of aphelion and perihelion for each body
	double aphelionArray[] = new double[numberOfParticles];
	double perihelionArray[] = new double[numberOfParticles];

	// determine clockwise/anitclockwise orbits for each body
	boolean clockwise[] = new boolean[numberOfParticles];
	for (int j=0; j<numberOfParticles; j++) {
	    if (Vector3D.vecCross(particleArray[j].getPosition(),particleArray[j].getVelocity()).getZ() > 0) {
		clockwise[j] = false;
	    }
	    else { clockwise[j] = true; }
	}

	/* 
	 * Loop over times steps
	 *
	 */

	for (int i=0; i<numberOfSteps; i++) {

	    // calc initial angles before the position update
	    for (int j=0; j<numberOfParticles; j++) {
		prevAngle[j] = Math.atan2(particleArray[j].getPosition().getY(), particleArray[j].getPosition().getX());
	    }

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
	    /*
	     * Test for j=0
	     */
	    for (int j=1; j < numberOfParticles; j++) {
		double separation = Particle3D.pSep(particleArray[0], particleArray[j]).mag();
		if (aphelionArray[j] < separation) {aphelionArray[j] = separation; }
		if (perihelionArray[j] > separation) {perihelionArray[j] = separation; }
	    }

	    // Prints every k-th position to VMD file
	    // calc min and max energy every k-th step to save on calc
	    if (i % printFrequency == 0) {	   
		output.printf(Particle3D.vmd(particleArray, stepNumber));

		energy = Particle3D.sysEnergy(particleArray);
		if (minEnergy > energy) { minEnergy = energy;  }
		if (maxEnergy < energy) { maxEnergy = energy;  }	
	    }

	    /*
	     * Start of orbit counter
	     */

	    for (int j=0; j<numberOfParticles; j++) {
		newAngle[j] = Math.atan2(particleArray[j].getPosition().getY(), particleArray[j].getPosition().getX());

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
	    }

	    /*
	     * End of orbit counter
	     */

	    stepNumber++;
	}

	/*
	 * End of loop over timesteps
	 */

	// Console output
	System.out.printf("\nEnergy fluctuation: %e\nThe ratio is %e\n\n", maxEnergy-minEnergy, Math.abs((maxEnergy-minEnergy)/((minEnergy+maxEnergy)/2)) );

	for (int i=0; i<numberOfParticles; i++) {
	    System.out.printf("Number of %s orbits: %f\n", particleArray[i].getLabel(), angleDiff[i]/(2*Math.PI));
	}

	System.out.println("\n");

	for (int i=0; i<numberOfParticles; i++) {
	    System.out.printf("%s orbit time: %f\n", particleArray[i].getLabel(), time/3600/24/(angleDiff[i]/(2*Math.PI)));
	}


	// Close the output file
	output.close();

    }
}
