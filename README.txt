---Nbody---


TO RUN:	java Nbody <input> <param> <output>


*********** 

Nbody simulates interactions and motions of an arbitrary number of particles.
In a more specific case, it is used to simulate our Solar System: Sun, all planets, Moon, Pluto, Halley Comet interacting through Newtonian gravity.

The package contains following files:

- inputAll.dat (Sun, all planets, Moon, Pluto and Halley Comet)
- inputElliptical.dat (Sun, Mercury, Venus)
- param.input
- outputAll.xyz (trajectory data from inputAll.dat)
- outputElliptical.xyz (trajectory data from inputElliptical.dat)
- expData.dat

Units used: time = days, distance = AU, mass = solar mass M☉.

***********

To run the program, the following are required:

1. Nbody.java
2. Particle.3D.java
3. Vector3D.java
4. Input file containing particles in the following format:
<label> <mass> <x-position> <y-position> <z-position> <x-velocity> <y-velocity> <z-velocity>
5. Parameter file containing, in this order:
<number of steps> <size of time step> <initial time> <print frequency> <value of gravitational constant> <length of Earth year in Earth days>
6. File containing experimental values in the same format as in 4. used for comparison
   
***********

How to run the program:

1. Compile the program by running
	javac Nbody.java	
in the terminal window. 
2. Run the simulation by running the following command:	
	java Nbody <input> <param> <output>
where <input> is the name of the input file (4.), <param> is the name of the file with parameters (5.) and <output> is the name of the output file, where trajectory data, in VMD format is written to.

IMPORTANT: when providing the name for the output file, .xyz has to be added at the end of the phrase. Otherwise the file will not be readable by VMD. 

3. While/after running the simulation, following information are displayed in the terminal window:

a) Uncorrected Total Linear Momentum
b) Total Mass of The System
c) Corrected Total Linear Momentum
d) Percent completion
e) Total run time
f) Energy fluctuation as a  % of the total system energy

In tables:

a) Mass
b) Orbit duration
c) Aphelion
d) Perihelion 
e) Orbit duration ratios: body:Earth
f) Verification of Kepler's 3rd Law
g) Total number of orbits
h) Comparison with experimental data

4. Once the simulation is complete, 2 output files will be created in the program's folder. The <output>.xyz is VMD ready and can be used to visually represent the trajectories. File with .energy suffix can be used to graphically represent fluctuation of total energy of the system with time (e.g. using xmgrace).

***********

