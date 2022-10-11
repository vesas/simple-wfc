package com.vesas.wfc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vesas.wfc.SimpleWFC.Constraints;
import com.vesas.wfc.SimpleWFC.DIR;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shaperend;
	Texture [] textures;

	private int grid[][][] = null;
	private int rots[][][] = null;
	private SimpleWFC wfc = null;

	float lighteffect = 0;
	int lighteffect_x = 0;
	int lighteffect_y = 0;

	int TILE_W = 128;
	int TILE_H = 64;
	int GRID_W = 7;
	int GRID_H = 10;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		shaperend = new ShapeRenderer();

		int tileCount = 19;
		boolean emptyAllowed = true;
		boolean rotationsAllowed = false;
		String basename = "flowc";

		if(emptyAllowed)
			// do not need texture for empty tile
			textures = new Texture[tileCount-1];
		else {
			textures = new Texture[tileCount];
		}

		for(int i = 0; i < textures.length; i++) {
			textures[i] = new Texture(String.format("%s%d.png", basename, i+1));
		}

		wfc = new SimpleWFC(GRID_W,GRID_H, tileCount, emptyAllowed, rotationsAllowed);
        wfc.setSeed(4);

        // wfc.setInputTexture("test_wfc_input1.png", 2, 2, 16, 16);

		wfc.setTilingVertical(false);
		wfc.setTilingHorizontal(false);

        Constraints constraints = new Constraints();

		constraints.addPort(1, DIR.S, 1);
		constraints.addPort(1, DIR.E, 1);

		constraints.addPort(2, DIR.S, 1);
		constraints.addPort(2, DIR.E, 1);

		constraints.addPort(3, DIR.W, 1);
		constraints.addPort(3, DIR.E, 1);

		constraints.addPort(4, DIR.N, 1);
		constraints.addPort(4, DIR.S, 1);

		constraints.addPort(5, DIR.W, 1);
		constraints.addPort(5, DIR.N, 1);

		constraints.addPort(6, DIR.N, 1);

		constraints.addPort(7, DIR.N, 1);

		constraints.addPort(8, DIR.W, 1);
		constraints.addPort(8, DIR.S, 1);

		constraints.addPort(9, DIR.N, 1);
		constraints.addPort(9, DIR.S, 1);
		constraints.addPort(9, DIR.W, 1);
		constraints.addPort(9, DIR.E, 1);

		constraints.addPort(10, DIR.W, 1);
		constraints.addPort(10, DIR.N, 1);

		constraints.addPort(11, DIR.W, 1);
		constraints.addPort(11, DIR.N, 1);

		constraints.addPort(12, DIR.E, 1);
		constraints.addPort(12, DIR.S, 1);

		constraints.addPort(13, DIR.W, 1);
		constraints.addPort(13, DIR.N, 1);
		constraints.addPort(13, DIR.E, 1);
		constraints.addPort(13, DIR.S, 1);

		constraints.addPort(14, DIR.W, 1);
		constraints.addPort(14, DIR.E, 1);
		constraints.addPort(14, DIR.S, 1);

		constraints.addPort(15, DIR.W, 1);
		constraints.addPort(15, DIR.N, 1);
		constraints.addPort(15, DIR.E, 1);

		constraints.addPort(16, DIR.W, 1);
		constraints.addPort(16, DIR.N, 1);

		constraints.addPort(17, DIR.E, 1);
		constraints.addPort(17, DIR.N, 1);

		constraints.addPort(18, DIR.E, 1);
		constraints.addPort(18, DIR.N, 1);
		constraints.addPort(18, DIR.W, 1);
		constraints.addPort(18, DIR.S, 1);


		wfc.setConstraints(constraints);
		wfc.printConstraints();
		grid = wfc.getGrid();
		rots = wfc.getRots();

		Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyTyped (char key) {

				if(key == ' ') {
					runRound();
				}
				if(key == 'g')  {
					wfc.printGrid();
				}
				if(key == 'c')  {
					wfc.printConstraints();
				}
                return true;
            }
        });
		
	}

	private void runRound() {
		System.out.println("SPACE!");

		wfc.runOneRound();
		grid = wfc.getGrid();
		rots = wfc.getRots();

		this.lighteffect_x = wfc.getLastModifiedX();
		this.lighteffect_y = wfc.getLastModifiedY();
		this.lighteffect = 0.6f;
	}

	@Override
	public void render () {

		ScreenUtils.clear(0.95f, 0.95f, 0.95f, 1);

		if(this.lighteffect > 0) {
			shaperend.begin(ShapeType.Filled);
			float t = this.lighteffect;
			shaperend.setColor(t,t,t*0.5f,1.0f);
			shaperend.rect(lighteffect_x*TILE_W, lighteffect_y*TILE_H, TILE_W, TILE_H);
			shaperend.end();
			this.lighteffect = this.lighteffect - 0.04f;
		}
		
		batch.begin();

		
		for(int x = 0; x < GRID_W; x++) {
			for(int y = 0; y < GRID_H; y++) {
			
				int [] possibilities = grid[x][y];
				int [] rotations = rots[x][y];

                for(int i= 0; i < possibilities.length;i++) {

					int rot = rotations[i];
					int tile = possibilities[i];

					if(tile > 0) {
						// bits 1-4
						for(int j = 0; j < 4; j++) {
							int temp = rot & (1 << (j));

							if(temp > 0) {
								Sprite s=new Sprite(textures[tile-1]);
								s.setOriginCenter();
								s.setRotation(-j * 90);
								s.setPosition(x*TILE_W,y*TILE_H);
								s.draw(batch);
							}
						}
					}
                }
			}
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
