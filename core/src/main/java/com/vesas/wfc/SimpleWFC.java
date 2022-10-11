package com.vesas.wfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class SimpleWFC {
    
    static public class Coord {
        public int x;
        public int y;
    }

    static public enum DIR {
        N,E,S,W;
        
        // rotates counter clockwise (times must be between 0 and 3, inclusive)
        public DIR rotateCCW(int times) {

            int ord = this.ordinal();

            for(int i = 0; i < times; i++) {
                ord--;
                if(ord < 0) {
                    ord = 3;
                }
            }
            return DIR.values()[ord];
        }

        // wrong
        // make it 1, 2, 4, 8
        public DIR fromBitMask(int mask) {

            if(mask == 0)
                return this;

            int ord = this.ordinal();

            if(mask == 1) {
                ord++;
                if(ord > 3) 
                    ord = 0;
                return DIR.values()[ord];
            }
                
            if(mask == 2) {
                ord = ord + 2;
                if(ord > 3) 
                    ord = ord -4;
                return DIR.values()[ord];
            }
                
            if(mask == 4)
                ord = ord + 3;
                if(ord > 3) 
                    ord = ord -4;
                return DIR.values()[ord];
        }

        public DIR opposite() {
            if(this == N) {
                return S;
            }
            else if(this == S) {
                return N;
            }
            else if(this == W) {
                return E;
            }
            else if(this == E) {
                return W;
            }

            return S;
        }
    }

    //  Combination of state and direction allowed from that state
    static public class StateDir {
        public int state;
        public DIR dir;

        @Override
        public boolean equals(Object o) {
            
            if (this == o)
                return true;
            
            if (o == null)
                return false;
            
            if (getClass() != o.getClass())
                return false;
            
            StateDir stateDir = (StateDir)o;
        
            return  this.state == stateDir.state && 
                    this.dir == stateDir.dir;
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int hash = 7;
            hash += PRIME * hash + this.state;
            hash += PRIME * hash + this.dir.ordinal();
            return hash;

        }

        public String toString() {
            return "" + this.state + "[" + this.dir + "]";
        }
    }

    static public class Constraints {

        Map<StateDir,Integer> ports = new HashMap<StateDir,Integer>();

        public List<Integer> getTransitions(int sourceTileId, DIR dir, int rotations) {
         
            int index = 1;
            while (rotations > 0)
            {
                if((rotations % 2) == 1)
                {
                    DIR tempDir = dir.fromBitMask(rotations);

                    StateDir stateDir = new StateDir();
                    stateDir.state = sourceTileId;
                    stateDir.dir = dir.fromBitMask(rotations);

                    /*
                    List<Integer> subarr = ports.get(stateDir);

                    if(subarr == null) {
                        subarr = new ArrayList<>();
                    }
                     */
                }
                index++;
                rotations /= 2;
            }

            
            return null;
        }

        public List<Integer> getTransitions(int sourceTileId, DIR dir) {

            StateDir stateDir = new StateDir();
            stateDir.state = sourceTileId;
            stateDir.dir = dir;

            /*
            List<Integer> subarr = ports.get(stateDir);

            if(subarr == null) {
                subarr = new ArrayList<>();
            }
            return subarr;
             */
            return null;
        }

        public void addPort(int source, DIR dir, int portId) {
            StateDir stateDir = new StateDir();
            stateDir.state = source;
            stateDir.dir = dir;
            ports.put(stateDir,portId);
        }

        public int getPort(int source, DIR dir) {
            StateDir stateDir = new StateDir();
            stateDir.state = source;
            stateDir.dir = dir;

            Integer port = ports.get(stateDir);

            if(port != null)
                return port;
            else
                return -1;
        }


        public void printConstraints() {

            Set<Entry<StateDir,Integer>> temp = this.ports.entrySet();

            System.out.println("constraints: ");

            for(Entry<StateDir, Integer> entry : temp) {

                System.out.println("" + entry.getKey() + " -> " + entry.getValue());
            }
            
        }
    }

    private Constraints constraints;

    public Constraints getConstraints() {
        return constraints;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    // grid of arrays of tile id's
    private int idGrid[][][] = null;

    // grid of arrays of tile rotations
    // rotation array contains 1,2,4,8 values superimposed
    // x, y, [array of rotation values]
    private int rotationGrid[][][] = null;

    private int width;
    private int height;

    private int tileCount = 0;

    // should the whole texture tile?
    private boolean tilingVertical = false;
    private boolean tilingHorizontal = false;

    private boolean emptyAllowed = true;

    public void setEmptyAllowed(boolean emptyAllowed) {
        this.emptyAllowed = emptyAllowed;
    }

    public void setTilingVertical(boolean tilingVertical) {
        this.tilingVertical = tilingVertical;
    }

    public void setTilingHorizontal(boolean tilingHorizontal) {
        this.tilingHorizontal = tilingHorizontal;
    }

    private Random random = new Random();

    public int [][][] getRots() {
        return rotationGrid;
    }

    public int [][][] getGrid() {
        return idGrid;
    }

    // rotations, two opposing ports must have the same rotations for them to match
    public int [][][] getRotationGrid() {
        return rotationGrid;
    }

    private SimpleWFC() {

    }

    public SimpleWFC(int w, int h, int tileCount, boolean emptyAllowed, boolean rotationsAllowed) {
        
        this.emptyAllowed = emptyAllowed;
        this.tileCount = tileCount;
        this.width = w;
        this.height = h;

        idGrid = new int[w][h][this.tileCount];
        rotationGrid = new int[w][h][this.tileCount];

        for(int i = 0; i < this.tileCount;i++) {
            // 1 = no rot
            // 2 = one rotation (CCW)
            // 4 = two CCW
            // 8 = three CCW
            if(rotationsAllowed)
                ALL_TILES.add(new TileAndRotation(i, 15));
            else
                ALL_TILES.add(new TileAndRotation(i, 1));
        }

        for(int temp_h = 0; temp_h < this.height;temp_h++) {
            for(int temp_w = 0; temp_w < this.width;temp_w++) {

                int [] possibilities = idGrid[temp_w][temp_h];

                for(int i= 0; i < possibilities.length;i++) {
                    if(emptyAllowed)
                        possibilities[i] = i;
                    else 
                        possibilities[i] = i + 1;
                }

                int [] rotations = rotationGrid[temp_w][temp_h];

                // 8 + 4 + 2 + 1 = 15
                for(int i= 0; i < rotations.length;i++) {
                    if(rotationsAllowed)
                        rotations[i] = 15;
                    else
                        rotations[i] = 1;
                }
            }
        }

    }

    private int lastModifiedX;
    private int lastModifiedY;

    public int getLastModifiedX() {
        return lastModifiedX;
    }

    public int getLastModifiedY() {
        return lastModifiedY;
    }

    public void setSeed(int val) {
        random = new Random(val);
    }

    // "local" entropy means entropy just for this square
    public int getLocalEntrypyAt(int x, int y) {
        int [] possibilities = idGrid[x][y];
        return possibilities == null ? 0 : possibilities.length;
    }

    // "local" entropy means entropy just for this square
    public int getLocalEntrypyAt(int pos) {

        int stride = this.width;
        int y = pos / stride;
        int x = pos % stride;

        int [] possibilities = idGrid[x][y];
        return possibilities == null ? 0 : possibilities.length;
    }

    // TODO: mod this to include surrounding cells (and edges)
    public int findLowEntrypy() {

        int stride = this.width;

        int lowestEntropy = 1231231231;
        int lowestEntropyPos = -1;

        for(int h = 0; h < this.height;h++) {
            for(int w = 0; w < this.width;w++) {

                int [] possibilities = idGrid[w][h];

                int temp = possibilities.length;

                if(temp > 1 && temp < lowestEntropy) {
                    lowestEntropy = temp;
                    lowestEntropyPos = h * stride + w;
                }
            }
        }

        return lowestEntropyPos;
    }

    public void setValuesAt(int x, int y, int ... values)
    {
        idGrid[x][y] = values;
    }

    public void setRotationsAt(int x, int y, int ... values)
    {
        rotationGrid[x][y] = values;
    }

    private void setValuesAt(int index, int [] values) {

        int stride = this.width;
        int y = index / stride;
        int x = index % stride;

        idGrid[x][y] = values;
    }

    private int [] getRandomValueAt(int index) {

        int stride = this.width;
        int y = index / stride;
        int x = index % stride;

        int [] temp = idGrid[x][y];

        int rand = random.nextInt(temp.length);

        int [] ret = { temp[rand] };
        return ret;
    }

    public void printConstraints() {
        this.constraints.printConstraints();
    }

    public void printGrid() {

        System.out.println("WFC GRID:");
        for(int h = this.height-1; h >= 0;h--) {
            for(int w = 0; w < this.width;w++) {

                int [] possibilities = idGrid[w][h];
                int pos_len = possibilities.length;

                System.out.print(" [");
                for (int i : possibilities) {
                    System.out.print("" + i );
                    if(i < (pos_len-1)) {
                        System.out.print(",");
                    }
                }
                System.out.print("]");
            }
            System.out.println("");
        }
    }

    public void printRotations() {
        System.out.println("ROTATION GRID:");
        for(int h = this.height-1; h >= 0;h--) {
            for(int w = 0; w < this.width;w++) {

                int [] rots = rotationGrid[w][h];
                int rots_len = rots.length;

                System.out.print(" [");
                int counter = 0;
                for (int rot : rots) {
                    System.out.print("" + rot );
                    if(counter < (rots_len-1)) {
                        System.out.print(",");
                    }
                    counter++;
                }
                System.out.print("]");
            }
            System.out.println("");
        }
    }

    private List<TileAndRotation> ALL_TILES = new ArrayList<TileAndRotation>();

    private List<TileAndRotation> allTiles() {

        List<TileAndRotation> allTiles = new ArrayList<TileAndRotation>();
        allTiles.addAll(ALL_TILES);
        return allTiles;
    }

    
    private Set<TileAndRotation> allTilesSet() {

        Set<TileAndRotation> allTiles = new HashSet<TileAndRotation>();
        allTiles.addAll(ALL_TILES);
        return allTiles;
    }
    /*
    private Set<Integer> allTilesSet() {

        Set<Integer> allTiles = new HashSet<Integer>();
        allTiles.addAll(ALL_TILES);
        return allTiles;
    }
    */

    // In x, y position, looking at target dir, which tiles are allowed at the target position?
    private void filterAllowedTilesByTarget(int x, int y, DIR dir, Set<TileAndRotation> currentAllowedTiles) {

        Iterator<TileAndRotation> it = currentAllowedTiles.iterator();

        int [] targetTiles = idGrid[x][y];
        int [] targetRots = rotationGrid[x][y];
        
        while(it.hasNext()) {

            TileAndRotation tileAndRot = it.next();
            
            // int retRots = tileAndRot.rot;
            int retRots = 0;

            for(int t = 0; t < targetTiles.length; t++) 
            {
                int targetTile = targetTiles[t];
                int targetRot = targetRots[t];

                if((targetRot & 1)== 1) {
                
                    int port = constraints.getPort(targetTile, dir);
                    
                    // match allowed tiles witht the target, remove any that don't match
                    if(port > -1 && contains(port, tileAndRot.tile, tileAndRot.rot, dir)) {
                        retRots = retRots | ~1;
                    }
                }
                if((targetRot & 2)== 2) {
                    int port = constraints.getPort(targetTile, dir.rotateCCW(1));
                    
                    // match allowed tiles witht the target, remove any that don't match
                    if(port > -1 && contains(port, tileAndRot.tile, tileAndRot.rot, dir)) {
                        retRots = retRots | ~2;
                    }
                }
    
                if((targetRot & 4)== 4) {
                    int port = constraints.getPort(targetTile, dir.rotateCCW(2));
                    
                    // match allowed tiles witht the target, remove any that don't match
                    if(port > -1 && contains(port, tileAndRot.tile, tileAndRot.rot, dir)) {
                        retRots = retRots | ~4;
                    }
                }
    
                if((targetRot & 8)== 8) {
                    int port = constraints.getPort(targetTile, dir.rotateCCW(3));
                    
                    // match allowed tiles witht the target, remove any that don't match
                    if(port > -1 && contains(port, tileAndRot.tile, tileAndRot.rot, dir)) {
                        retRots = retRots | ~8;
                    }
                }
            }

            if(retRots == 0) {
                it.remove();
            }
            else {
                tileAndRot.rot = retRots;
            }
        }
    }

    // In x, y position, looking at target dir, which tiles are allowed at the target position?
    private Set<TileAndRotation> allAllowedTilesAtTarget(int x, int y, DIR dir) {

        // 1) get all tiles at location
        int [] sourceTiles = idGrid[x][y];
        int [] sourceRotations = rotationGrid[x][y];

        Set<TileAndRotation> ret = new HashSet<TileAndRotation>();

        // iterate the stack of current possible tiles at the source
        for(int i = 0; i < sourceTiles.length; i++) {
            int val = sourceTiles[i];
            int possibleRotations = sourceRotations[i];

            int rotsRet = 0;
            // 1 == N, 2 == E, 4 == S, 8 == W
            if((possibleRotations & 1) == 1) {
                List<Integer> transitions = constraints.getTransitions(val, dir);
            }
            if((possibleRotations & 2) == 2) {
                List<Integer> transitions = constraints.getTransitions(val, dir.fromBitMask(2));
            }
            

            // no port defined transitions found -> all are still allowed
            // if(transitions.isEmpty()) {
                // return allTilesSet();
            // }
            // else {
                // ret.addAll(transitions);
            // }

            int qwe = 0;
        }

        return ret;
    }

    private boolean contains(int port, int targetTile, int targetRot, DIR dir) {
        
        if((targetRot & 1) == 1) {
            int targetPort = constraints.getPort(targetTile, dir);
            if(targetPort == port)
                return true;
        }
        if((targetRot & 2) == 2) {
            int targetPort = constraints.getPort(targetTile, dir.rotateCCW(1));
            if(targetPort == port)
                return true;
        }
        if((targetRot & 4) == 4) {
            int targetPort = constraints.getPort(targetTile, dir.rotateCCW(2));
            if(targetPort == port)
                return true;
        }
        if((targetRot & 8) == 8) {
            int targetPort = constraints.getPort(targetTile, dir.rotateCCW(3));
            if(targetPort == port)
                return true;
        }

        return false;
    }

    private boolean containsOnlyPorts(int [] targetTiles, int[] targetRots, DIR dir) {

        for(int i = 0; i < targetTiles.length; i++) {

            int targetTile = targetTiles[i];
            int targetRot = targetRots[i];

            if((targetRot & 1) == 1) {
                int targetPort = constraints.getPort(targetTile, dir);
                if(targetPort == -1)
                    return false;
            }
            if((targetRot & 2) == 2) {
                int targetPort = constraints.getPort(targetTile, dir.rotateCCW(1));
                if(targetPort == -1)
                    return false;
            }
            if((targetRot & 4) == 4) {
                int targetPort = constraints.getPort(targetTile, dir.rotateCCW(2));
                if(targetPort == -1)
                    return false;
            }
            if((targetRot & 8) == 8) {
                int targetPort = constraints.getPort(targetTile, dir.rotateCCW(3));
                if(targetPort == -1)
                    return false;
            }
        }
        
        return true;
    }

    private boolean containsAny(int port, int [] targetTiles, int[] targetRots, DIR dir) {
        
        // for each target tile check all rots and see if it's ports contains the requested port
        for(int i = 0; i < targetTiles.length; i++) {

            int targetTile = targetTiles[i];
            int targetRot = targetRots[i];

            if((targetRot & 1) == 1) {
                int targetPort = constraints.getPort(targetTile, dir);
                if(targetPort == port)
                    return true;
            }
            if((targetRot & 2) == 2) {
                int targetPort = constraints.getPort(targetTile, dir.rotateCCW(1));
                if(targetPort == port)
                    return true;
            }
            if((targetRot & 4) == 4) {
                int targetPort = constraints.getPort(targetTile, dir.rotateCCW(2));
                if(targetPort == port)
                    return true;
            }
            if((targetRot & 8) == 8) {
                int targetPort = constraints.getPort(targetTile, dir.rotateCCW(3));
                if(targetPort == port)
                    return true;
            }
        }
        

        return false;
    }

    // In x, y position, looking at dir, which tiles are allowed at the x,y position?
    private void filterAllowedTilesAtSource(int x, int y, DIR dir, Set<TileAndRotation> currentAllowedTiles) {
       
        Iterator<TileAndRotation> it = currentAllowedTiles.iterator();
        while(it.hasNext()) {

            TileAndRotation tileAndRot = it.next();
            
            int retRots = tileAndRot.rot;

            // check each target 
            int [] targetTiles = null;
            int [] targetRots = null;

            int targetX, targetY;

            if(dir == DIR.N) {

                targetX = x;
                if(y == (this.height-1)) {
                    targetY = 0;
                }
                else {
                    targetY = y + 1;
                }
                targetTiles = idGrid[targetX][targetY];
                targetRots = rotationGrid[targetX][targetY];
            }
            else if(dir == DIR.S) {
                targetX = x;

                if(y == 0) {
                    targetY = this.height-1;
                }
                else {
                    targetY = y - 1;
                }

                targetTiles = idGrid[targetX][targetY];
                targetRots = rotationGrid[targetX][targetY];
            }
            else if(dir == DIR.W) {

                targetY = y;
                if(x == 0) {
                    targetX = this.width - 1;
                }
                else {
                    targetX = x - 1;
                }
                targetTiles = idGrid[targetX][targetY];
                targetRots = rotationGrid[targetX][targetY];
            }
            else if(dir == DIR.E) {

                targetY = y;
                if(x == (this.width-1)) {
                    targetX = 0;
                }
                else {
                    targetX = x + 1;
                }
                targetTiles = idGrid[targetX][targetY];
                targetRots = rotationGrid[targetX][targetY];
            }

            if((tileAndRot.rot & 1)== 1) {
                int port = constraints.getPort(tileAndRot.tile, dir);
                // match allowed tiles witht the target, remove any that don't match
                if(port > -1) {
                    if(!containsAny(port, targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~1;
                    }
                }
                else {
                    if(containsOnlyPorts(targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~1;
                    }
                }
            }
            if((tileAndRot.rot & 2)== 2) {
                int port = constraints.getPort(tileAndRot.tile, dir.rotateCCW(1));
                
                if(port > -1) {
                    if(!containsAny(port, targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~2;
                    }
                }
                else {
                    if(containsOnlyPorts(targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~2;
                    }
                }
            }

            if((tileAndRot.rot & 4)== 4) {
                int port = constraints.getPort(tileAndRot.tile, dir.rotateCCW(2));
                
                if(port > -1) {
                    if(!containsAny(port, targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~4;
                    }
                }
                else {
                    if(containsOnlyPorts(targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~4;
                    }
                }
            }

            if((tileAndRot.rot & 8)== 8) {
                int port = constraints.getPort(tileAndRot.tile, dir.rotateCCW(3));
                
                if(port > -1)
                {
                    if(!containsAny(port, targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~8;
                    }
                }
                else {
                    if(containsOnlyPorts(targetTiles, targetRots, dir.opposite())) {
                        retRots = retRots & ~8;
                    }
                } 
            }

            if(retRots == 0) {
                it.remove();
            }
            else {
                tileAndRot.rot = retRots;
            }
        }
    }

    
    private Set<TileAndRotation> allAllowedTilesAtSource(int x, int y, DIR dir) {
        // 1) get all tiles at location
        int [] sourceTiles = idGrid[x][y];
        int [] sourceRotations = rotationGrid[x][y];

        Set<TileAndRotation> ret = new HashSet<TileAndRotation>();

        // 2) all from constraints which tiles are allowed for all tiles at location

        for(int i = 0; i < sourceTiles.length; i++) {
            int val = sourceTiles[i];

            List<Integer> transitions = constraints.getTransitions(val, dir);

            int qwe = 0;
            // no port defined transitions found -> all are still allowed
            if(transitions.isEmpty()) {
                qwe = 0;
                // ret.add(val);
            }
            else {
                if(dir == DIR.N) {
                    // check each target 
                    int [] targetTiles = idGrid[x][y+1];

                    
                    // if(containsAny(transitions, targetTiles))
                        qwe = 0;
                        // ret.add(val);
                }
                else if(dir == DIR.S) {
                    int [] targetTiles = idGrid[x][y-1];
                    // if(containsAny(transitions, targetTiles))
                        qwe = 0;
                        // ret.add(val);
                }
                else if(dir == DIR.W) {
                    int [] targetTiles = idGrid[x-1][y];
                    // if(containsAny(transitions, targetTiles))
                        qwe = 0;
                        // ret.add(val);
                }
                else if(dir == DIR.E) {
                    int [] targetTiles = idGrid[+1][y];
                    // if(containsAny(transitions, targetTiles))
                        qwe = 0;    
                        // ret.add(val);
                }
            }
        }

        return ret;
    }


    public void setPos(int x, int y, int value) {
        int [] temp = { value };
        idGrid[x][y] = temp;
    }

    public void setRotation(int x, int y, int value) {
        int [] temp = { value };
        rotationGrid[x][y] = temp;
    }

    private boolean atLeftEdge(int pos) {
        int stride = this.width;
        int y = pos / stride;
        int x = pos % stride;
        return x==0;
    }

    private boolean atRightEdge(int pos) {
        int stride = this.width;
        int y = pos / stride;
        int x = pos % stride;
        return x==(this.width-1);
    }

    private boolean atTopEdge(int pos) {
        int stride = this.width;
        int y = pos / stride;
        int x = pos % stride;
        return y==(this.height-1);
    }

    private boolean atBottomEdge(int pos) {
        int stride = this.width;
        int y = pos / stride;
        int x = pos % stride;
        return y==0;
    }

    // filter out ports pointing at dir, because we are edge looking at dir
    private void filterOutPorts(int x, int y, DIR dir, Set<TileAndRotation> currentAllowedTiles) {

        Iterator<TileAndRotation> it = currentAllowedTiles.iterator();
        while(it.hasNext()) {

            TileAndRotation tileAndRot = it.next();
            
            int retRots = tileAndRot.rot;

            if((tileAndRot.rot & 1)== 1) {
                // List<Integer> transitions = constraints.getTransitions(tileAndRot.tile, dir);
                int port = constraints.getPort(tileAndRot.tile, dir);
                
                // we have a port
                if(port > -1) {
                    retRots = retRots & ~1;
                }
            }
            if((tileAndRot.rot & 2)== 2) {
                int port = constraints.getPort(tileAndRot.tile, dir.rotateCCW(1));
                
                // we have a port
                if(port > -1) {
                    retRots = retRots & ~2;
                }
            }

            if((tileAndRot.rot & 4)== 4) {
                int port = constraints.getPort(tileAndRot.tile, dir.rotateCCW(2));
                
                // we have a port
                if(port > -1) {
                    retRots = retRots & ~4;
                }
            }

            if((tileAndRot.rot & 8)== 8) {
                int port =  constraints.getPort(tileAndRot.tile, dir.rotateCCW(3));
                
                // we have a port
                if(port > -1) {
                    retRots = retRots & ~8;
                }
            }

            if(retRots == 0) {
                it.remove();
            }
            else {
                tileAndRot.rot = retRots;
            }
        }

    }

    private void filterTileChoices(int x, int y, DIR dir, Set<TileAndRotation> currentAllowedTiles) {

        if(dir == DIR.N) {
            
            if(y == (this.height-1) && !this.tilingVertical) {
                // At top edge
                filterOutPorts(x,y,dir, currentAllowedTiles);
            }
            else {
                // not at top edge or tiling
                filterAllowedTilesAtSource(x,y,dir, currentAllowedTiles);
                // filterAllowedTilesByTarget(x,y+1, dir.opposite(), currentAllowedTiles);
            }
        }
        else if(dir == DIR.S) {

            if(y == 0 && !this.tilingVertical) {
                // At bottom edge
                filterOutPorts(x,y,dir, currentAllowedTiles);
            }
            else {
                // not at bottom edge or tiling
                filterAllowedTilesAtSource(x,y,dir, currentAllowedTiles);
                // filterAllowedTilesByTarget(x,y-1, dir.opposite(), currentAllowedTiles);

            }
        }
        else if(dir == DIR.W) {
            if(x == 0 && !this.tilingHorizontal) {
                // At left edge
                filterOutPorts(x,y,dir, currentAllowedTiles);
            }
            else {
                // not at left edge, or tiling
                filterAllowedTilesAtSource(x,y,dir, currentAllowedTiles);
                // filterAllowedTilesByTarget(x-1,y, dir.opposite(), currentAllowedTiles);

            }
        }
        else if(dir == DIR.E) {
            if(x == (this.width-1) && !this.tilingHorizontal) {
                // At right edge
                filterOutPorts(x,y,dir, currentAllowedTiles);
            }
            else {
                // not at right edge, or tiling
                filterAllowedTilesAtSource(x,y,dir, currentAllowedTiles);
                // filterAllowedTilesByTarget(x+1,y, dir.opposite(), currentAllowedTiles);

            }
        }
    }

    public class TileAndRotation {

        public TileAndRotation(int tile, int rotation) {
            this.tile = tile;
            this.rot = rotation;
        }
        
        // just tile id
        public int tile;
        // 0, 1, 2, 4 [N, E, S, W]
        public int rot;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + rot;
            result = prime * result + tile;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TileAndRotation other = (TileAndRotation) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
                return false;
            if (rot != other.rot)
                return false;
            if (tile != other.tile)
                return false;
            return true;
        }
        private SimpleWFC getEnclosingInstance() {
            return SimpleWFC.this;
        }

        @Override
        public String toString() {
            return "TileAndRotation [rot=" + rot + ", tile=" + tile + "]";
        }
    }

    private Set<TileAndRotation> tilesAndRotations(int x, int y) {
        int [] sourceTiles = idGrid[x][y];
        int [] sourceRotations = rotationGrid[x][y];

        // LinkedHashSet just for debugging repeatability with fixed seed
        Set<TileAndRotation> ret = new LinkedHashSet<TileAndRotation>();

        for(int i = 0; i < sourceTiles.length; i++) {
            int tile = sourceTiles[i];
            int rots = sourceRotations[i];
            TileAndRotation t = new TileAndRotation(tile, rots);
            ret.add(t);
        }

        return ret;
    }

    private void observe() {

        int pos = this.findLowEntrypy();

        int lowestEntropy = -1;
        if(pos > -1)
            lowestEntropy = this.getLocalEntrypyAt(pos);

        // not found, we finished?
        if(lowestEntropy == -1) {
            finished = true;
            return;
        }

        int stride = this.width;
        int y = pos / stride;
        int x = pos % stride;

        Set<TileAndRotation> allAllowedTiles = this.tilesAndRotations(x,y);
                // check allowedTiles against all directions, and remove impossible ones

        filterTileChoices(x, y, DIR.N, allAllowedTiles);
        filterTileChoices(x, y, DIR.S, allAllowedTiles);
        filterTileChoices(x, y, DIR.E, allAllowedTiles);
        filterTileChoices(x, y, DIR.W, allAllowedTiles);

        // Ok, multiple choices available, now OBSERVE and collapse to one choice
        if(allAllowedTiles.size() > 1) {

            int rand = random.nextInt(allAllowedTiles.size());

            int currentIndex = 0;
            for(TileAndRotation in : allAllowedTiles) {

                if(rand == currentIndex) {

                    this.setPos(x,y, in.tile);

                    int bitsOn = bitsOn(in.rot);
                    int randRot = random.nextInt(bitsOn);

                    int counter = 0;
                    if((in.rot & 8) == 8) {
                        if(counter == randRot) {
                            this.setRotation(x,y, 8);
                        }
                        counter++;
                    }
                    if((in.rot & 4) == 4) {
                        if(counter == randRot) {
                            this.setRotation(x,y, 4);
                        }
                        counter++;
                    }
                    if((in.rot & 2) == 2) {
                        if(counter == randRot) {
                            this.setRotation(x,y,2);
                        }
                        counter++;
                    }
                    if((in.rot & 1) == 1) {
                        if(counter == randRot) {
                            this.setRotation(x,y, 1);
                        }
                        counter++;
                    }

                    this.lastModifiedX = x;
                    this.lastModifiedY = y;
                    break;
                }

                currentIndex++;
            }
        }
        else if(allAllowedTiles.size() == 1) {

            for(TileAndRotation in : allAllowedTiles) {

                this.setPos(x,y, in.tile);

                int bitsOn = bitsOn(in.rot);
                int randRot = random.nextInt(bitsOn);

                int counter = 0;
                if((in.rot & 8) == 8) {
                    if(counter == randRot) {
                        this.setRotation(x,y, 8);
                    }
                    counter++;
                }
                if((in.rot & 4) == 4) {
                    if(counter == randRot) {
                        this.setRotation(x,y, 4);
                    }
                    counter++;
                }
                if((in.rot & 2) == 2) {
                    if(counter == randRot) {
                        this.setRotation(x,y, 2);
                    }
                    counter++;
                }
                if((in.rot & 1) == 1) {
                    if(counter == randRot) {
                        this.setRotation(x,y, 1);
                    }
                    counter++;
                }

                this.lastModifiedX = x;
                this.lastModifiedY = y;
                break;
            }
        }
        else {
            System.out.println("ERROR: 0 tiles possible");
        }

        int qwe = 0;

    }

    private int bitsOn(int val) {
        int count = 0;
        while (val > 0) {
            count += val & 1;
            val >>= 1;
        }
        return count;
    }

    private void propagate() {
        
        Queue<Coord> coords = new LinkedList<Coord>();

        int x = this.lastModifiedX;
        int y = this.lastModifiedY;



        // flood fill possible values (ie. remove values that cannot exist bec of constraints)
        // temp
        finished = true;
    }

    private boolean isFinished() {

        boolean ret = true;
        for(int h = 0; h < this.height;h++) {
            for(int w = 0; w < this.width;w++) {

                int [] possibilities = idGrid[w][h];
                int pos_len = possibilities.length;

                if(pos_len > 1) {
                    ret = false;
                }
            }
        }

        return ret;
    }

    public void runOneRound() {

        observe();

        if(!finished)
            propagate();

        finished = this.isFinished();
    }

    public void run() {

        this.finished = false;

        while(!finished) {
            observe();

            if(!finished)
                propagate();

            finished = this.isFinished();
        }

    }

    private boolean finished = false;

}
