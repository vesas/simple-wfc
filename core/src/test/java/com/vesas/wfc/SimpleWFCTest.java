package com.vesas.wfc;

import org.junit.jupiter.api.Test;

import com.vesas.wfc.SimpleWFC.Constraints;
import com.vesas.wfc.SimpleWFC.DIR;

public class SimpleWFCTest {
    

    @Test
    public void testRotation1() {
        SimpleWFC wfc = new SimpleWFC(2,1, 3, true);
        
        Constraints constraints = new Constraints();
        wfc.setSeed(999);
        
        // tile 0 is the empty tile
        constraints.addPort(1, DIR.S, 1);
        constraints.addPort(2, DIR.N, 1);

        wfc.setValuesAt(0,0,1);
        wfc.setRotationsAt(0,0,8);

        wfc.setValuesAt(1,0,0,2);
        wfc.setRotationsAt(1,0,15,15);
        
        wfc.setConstraints(constraints);
        wfc.printConstraints();
        wfc.printGrid();
        wfc.printRotations();

        wfc.runOneRound();
        wfc.printGrid();
        wfc.printRotations();

    }

    @Test
    public void testRotation2() {
        SimpleWFC wfc = new SimpleWFC(2,2, 3, true);
        
        Constraints constraints = new Constraints();
        wfc.setSeed(3);
        
        // tile 0 is the empty tile
        // tile 1 has one port facing south
        constraints.addPort(1, DIR.S, 1);
        // tile 2 has two ports, south and west
        constraints.addPort(2, DIR.S, 1);
        constraints.addPort(2, DIR.W, 1);

        // wfc.setValuesAt(0,0,2);
        // wfc.setRotationsAt(1,0,15,15);
        
        wfc.setConstraints(constraints);
        wfc.printConstraints();
        wfc.printGrid();
        wfc.printRotations();

        wfc.runOneRound();
        wfc.printGrid();
        wfc.printRotations();

        wfc.runOneRound();
        wfc.printGrid();
        wfc.printRotations();

        wfc.runOneRound();
        wfc.printGrid();
        wfc.printRotations();

        wfc.runOneRound();
        wfc.printGrid();
        wfc.printRotations();

    }

    @Test
    public void testRotation() {
        SimpleWFC wfc = new SimpleWFC(2,1, 3, true);
        
        Constraints constraints = new Constraints();
        wfc.setSeed(999);
        
        // tile 0 is the empty tile
        constraints.addPort(1, DIR.S, 1);
        constraints.addPort(2, DIR.N, 1);
        
        wfc.setConstraints(constraints);
        wfc.printConstraints();
        wfc.printGrid();
        wfc.printRotations();

        wfc.runOneRound();
        wfc.printGrid();
        wfc.printRotations();

        wfc.runOneRound();
        wfc.printGrid();
        wfc.printRotations();
    }

    @Test
    public void test1() {
        

        SimpleWFC wfc = new SimpleWFC(1,2, 3, true);
        wfc.setSeed(5115);

        Constraints constraints = new Constraints();
        
        constraints.addPort(1, DIR.S, 2);


        wfc.setConstraints(constraints);
        wfc.printConstraints();
        wfc.printGrid();

        // ############
        // ROUNDS
        // ############
        wfc.runOneRound();
        wfc.printGrid();

        wfc.runOneRound();
        wfc.printGrid();

        wfc.runOneRound();
        wfc.printGrid();


    }

    @Test
    public void test2() {
        SimpleWFC wfc = new SimpleWFC(2,2, 2, true);
        wfc.setPos(1,1, 0);
        wfc.printGrid();
    }
}
