package org.spiritsofts.shogun.board.view;

import org.spiritsofts.shogun.R;
import org.spiritsofts.shogun.board.model.Pawn;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {

	private static final int BLACK_SHOGUN_ID = 20;
	private static final int WHITE_SHOGUN_ID = 10;
	private static final int ICON_HEIGHT = 40;
	private static final int ICON_WIDTH = 40;

	private SparseIntArray white;
	private SparseIntArray black;
	private SparseIntArray positions;
	private int pawnSelected = 0;
	private int posSelected = -1;
	private int whiteShogun;
	private int blackShogun;
	private boolean whiteTurn = true;

	private SparseBooleanArray possibleMoves;

	public BoardView(Context context) {
		super(context);
		resetView();
	}

	public SparseIntArray[] getSaveInfo() {
		return new SparseIntArray[] { positions, white, black };
	}
	
	public void switchTurn(){
		whiteTurn = !whiteTurn;
	}

	public void resumeSavedView(SparseIntArray[] load) {
		positions = load[0];
		white = load[1];
		black = load[2];
		for (int i = 0; i < 64; i++) {
			int toTest = positions.get(i);
			if (toTest == 10) {
				whiteShogun = i;
			} else if (toTest == 20) {
				blackShogun = i;
			}
		}
	}
	
	/**
	 * Is the pawn can take here ?
	 */
	public boolean isPawnTaken(int position){
		int pawn = positions.get(position);
		if (pawnSelected != pawn) {
			return possibleMoves.get(position);
		}
		return false;
	}
	
	/**
	 * Is the pawn can be move here ?
	 * @param position
	 * @return
	 */
	public boolean isPawnMoving(int position){
			return (pawnSelected != 0 && possibleMoves.get(position));
	}

	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		boolean returnB = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			int iconicPosition = realToIconic(event.getX(), event.getY());
			int pawn = positions.get(iconicPosition);
			if (pawn > 1) { // If selection
				if (pawnSelected == pawn) { // Deselection
					clearSelected();
				} else {
					if (!possibleMoves.get(iconicPosition)) { // PAWN ALREADY SEL
						addPossibleMoves(pawn, iconicPosition);
						returnB = true;
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			invalidate();
			returnB = true;
			break;
		}
		return returnB;
	}

	/**
	 * Graphic move
	 * 
	 * @param toMove
	 * @param iconicPosition
	 * @param oldPos
	 */
	public boolean moveHumanPawn(int iconicPosition,boolean isWhite) {
		int oldPos = posSelected;
		int toMove = pawnSelected;
		Pawn pawn = new Pawn(toMove, iconicPosition, black, white);
		if (pawn.isWhite() == isWhite){ // Avoid white to move black
			positions.put(oldPos, 0);
			positions.put(iconicPosition, toMove);
			if (pawn.isWhite()) {
				white.put(toMove - 10, pawn.getNextNbMove());
				if (pawn.isShogun()) {
					whiteShogun = iconicPosition;
				}
			} else {
				black.put(toMove - 20, pawn.getNextNbMove());
				if (pawn.isShogun()) {
					blackShogun = iconicPosition;
				}
			}
			clearSelected();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Graphic move for IA
	 * 
	 * @param toMove
	 * @param iconicPosition
	 * @param oldPos
	 */
	public boolean moveIAPawn(int pawnId,int targetPosition) {
		addPossibleMoves(pawnId,positions.get(pawnId));
		if (moveHumanPawn(targetPosition,false)){
			return true;
		} else {
			clearSelected();
			return false;
		}
	}
	
	/**
	 * "Lighten" all possible moves
	 * 
	 * @param pawn
	 */
	private void addPossibleMoves(int pawnId, int iconicPosition) {
		Pawn pawn = new Pawn(pawnId, iconicPosition, black, white);
		if (whiteTurn == pawn.isWhite()){
			pawnSelected = pawnId; // Selection
			posSelected = iconicPosition;
			possibleMoves.clear();
			int[] moves = pawn.getPossibleMove(positions);
			for (int it = 0; it < 4; it++) {
				if (moves[it] != 99) {
					possibleMoves.put(moves[it], true);
				}
			}
		}
	}

	/**
	 * 00 01 02 03 04 05 06 07 
	 * 08 09 10 11 12 13 14 15 
	 * 16 17 18 19 20 21 22 23
	 * 24 25 26 27 28 29 30 31 
	 * 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47
	 * 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63
	 */
	private void resetView() {
		white = createPreFilledArray();
		black = createPreFilledArray();
		positions = new SparseIntArray(64);
		possibleMoves = new SparseBooleanArray(64);
		// Put the whites into position
		positions.put(0, WHITE_SHOGUN_ID);
		positions.put(8, 11);
		positions.put(16, 12);
		positions.put(24, 13); // White Shogun !!!
		positions.put(32, 14);
		positions.put(40, 15);
		positions.put(48, 16);
		positions.put(56, 17);
		// Put the black into position
		positions.put(7, 27);
		positions.put(15, 26);
		positions.put(23, 25);
		positions.put(31, 24);
		positions.put(39, 23); // Black Shogun !!!
		positions.put(47, 22);
		positions.put(55, 21);
		positions.put(63, BLACK_SHOGUN_ID);
		whiteShogun = 0;
		blackShogun = 63;
	}

	private SparseIntArray createPreFilledArray() {
		SparseIntArray array = new SparseIntArray(8);
		array.put(0, 1);
		array.put(1, 2);
		array.put(2, 3);
		array.put(3, 4);
		array.put(4, 4);
		array.put(5, 3);
		array.put(6, 2);
		array.put(7, 1);
		return array;
	}

	/**
	 * The view is drawing
	 */
	protected void onDraw(Canvas canvas) {
		for (int pos = 0; pos < 64; pos++) {
			int pawn = positions.get(pos);
			int[] realPos = null;
			if (possibleMoves.get(pos) || pos == posSelected) { // Position
				// selected
				realPos = iconicToReal(pos);
				canvas.drawBitmap(getPossibleMoveBitmap(), realPos[0],
						realPos[1], null);
			}
			if (pawn > 1) { // If something on the position : Draw it
				realPos = iconicToReal(pos);
				canvas.drawBitmap(getPawnAsBitmap(pawn), realPos[0],
						realPos[1], null);
			}
			if (whiteShogun == pos || blackShogun == pos) {
				canvas.drawBitmap(getCrown(), realPos[0], realPos[1], null);
			}
		}
	}

	/**
	 * Get a possible move bitmap
	 * 
	 * @return
	 */
	private Bitmap getPossibleMoveBitmap() {
		Drawable drawable = getResources().getDrawable(R.drawable.selected);
		return populateDrawable(drawable);
	}

	/**
	 * Get a crown bitmap
	 * 
	 * @return
	 */
	private Bitmap getCrown() {
		Drawable drawable = getResources().getDrawable(R.drawable.crown);
		drawable.setBounds(0, 0, ICON_WIDTH, ICON_HEIGHT);
		return populateDrawable(drawable);
	}

	/**
	 * Get the pawn as a Bitmap return null if nothing on this location
	 * 
	 * @param pawn
	 * @return
	 */
	private Bitmap getPawnAsBitmap(int pawn) {
		Resources res = getResources();
		Pawn pawnPOJO = new Pawn(pawn, black, white);
		if (pawnPOJO.getNbMove() != 0) {
			Drawable drawable = getDrawablePawn(res, pawnPOJO.getNbMove(),
					pawnPOJO.isWhite());
			return populateDrawable(drawable);
		} else {
			return null;
		}
	}

	/**
	 * Get a Drawable pawn according to its position and color
	 * 
	 * @param res
	 * @param nbPos
	 * @param white
	 * @return
	 */
	private Drawable getDrawablePawn(Resources res, int nbPos, boolean white) {
		Drawable drawable = null;
		if (white) {
			switch (nbPos) {
			case 1:
				drawable = res.getDrawable(R.drawable.blue_1);
				break;
			case 2:
				drawable = res.getDrawable(R.drawable.blue_2);
				break;
			case 3:
				drawable = res.getDrawable(R.drawable.blue_3);

				break;
			case 4:
				drawable = res.getDrawable(R.drawable.blue_4);
				break;
			}
		} else {
			switch (nbPos) {
			case 1:
				drawable = res.getDrawable(R.drawable.red_1);
				break;
			case 2:
				drawable = res.getDrawable(R.drawable.red_2);
				break;
			case 3:
				drawable = res.getDrawable(R.drawable.red_3);
				break;
			case 4:
				drawable = res.getDrawable(R.drawable.red_4);
				break;
			}
		}
		return drawable;
	}

	/**
	 * Transform the drawable into a BitMap
	 * 
	 * @param drawable
	 * @return
	 */
	private Bitmap populateDrawable(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(ICON_WIDTH, ICON_HEIGHT,
				Bitmap.Config.ARGB_8888);
		drawable.setBounds(0, 0, ICON_WIDTH, ICON_HEIGHT);
		Canvas canvas = new Canvas(bitmap);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * No more selected
	 */
	private void clearSelected() {
		pawnSelected = 0;
		posSelected = -1;
		possibleMoves.clear();
	}
	
	public boolean isWhiteShogun(int position) {
		return (whiteShogun == position);
	}

	public boolean isBlackShogun(int position) {
		return (blackShogun == position);
	}

	/**
	 * Transform a real (x,y from sensor) location into an iconic location
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int realToIconic(float x, float y) {
		int deca = (int) Math.floor(x / ICON_WIDTH);
		int unite = (int) Math.floor(y / ICON_WIDTH);
		return 8 * unite + deca;
	}

	/**
	 * Transform an iconic location into a real location
	 * 
	 * @param number
	 * @return
	 */
	public int[] iconicToReal(int number) {
		int collumn = number % 8;
		int line = (int) Math.floor(number / 8);
		return new int[] { collumn * ICON_HEIGHT, line * ICON_WIDTH };
	}

}
