package org.spiritsofts.shogun.board.view;

import org.spiritsofts.shogun.R;
import org.spiritsofts.shogun.board.model.Pawn;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {

	private static final int ICON_HEIGHT = 40;
	private static final int ICON_WIDTH = 40;
	
	private Integer posSelected = null;
	private boolean whiteTurn = true;
	SparseArray<Integer> siTac = new SparseArray<Integer>(64);
	SparseBooleanArray possibleMoves = new SparseBooleanArray(64);
	private Boolean winnerWhite = null;

	public BoardView(Context context) {
		super(context);
		resetView();
	}

	public SparseArray<Integer> getSaveInfo() {
		return siTac;
	}
	
	public void switchTurn(){
		whiteTurn = !whiteTurn;
	}

	public void resumeSavedView(SparseIntArray[] load) {
		// TO IMPLEMENTS USING MODEL
	}
	
	/**
	 * Is the pawn can be move here ?
	 * @param position
	 * @return
	 */
	public boolean isPawnMoving(int position){
			return (posSelected != null && possibleMoves.get(position));
	}

	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		boolean returnB = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			int eventPosition = realToIconic(event.getX(), event.getY());
			Integer pawn = siTac.get(eventPosition);
			if (pawn != null) { // If selection
				if (posSelected!= null && posSelected == eventPosition) { // Deselection
					clearSelected();
				} else {
					if (!possibleMoves.get(eventPosition)) { // PAWN ALREADY SEL
						addPossibleMoves(pawn, eventPosition);
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
	public boolean moveHumanPawn(int targetPosition,boolean isWhite) {
		Integer oldPos = posSelected;
		if (oldPos != null){
			Pawn pawn = new Pawn(siTac.get(posSelected),posSelected);
			if (pawn.isWhite() == isWhite){ // Avoid white to move black
				siTac.put(oldPos,null);
				Integer stateTaken = siTac.get(targetPosition);
				if (stateTaken != null){
					Pawn pawnTaken = new Pawn(stateTaken);
					if (pawnTaken.isShogun()){
						winnerWhite = isWhite;
					}
				}
				siTac.put(targetPosition,pawn.getNextState());
				clearSelected();
				return true;
			} else {
				return false;
			}
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
	public boolean moveIAPawn(int pawnState,int originPostion,int targetPosition) {
		addPossibleMoves(pawnState,siTac.get(originPostion));
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
	private void addPossibleMoves(int pawnState, int iconicPosition) {
		Pawn pawn = new Pawn(pawnState, iconicPosition);
		if (whiteTurn == pawn.isWhite()){
			posSelected = iconicPosition;
			possibleMoves.clear();
			Integer[] moves = pawn.getPossibleMove(siTac);
			for (int it = 0; it < 4; it++) {
				if (moves[it] != null) {
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
	 * 32 33 34 35 36 37 38 39 
	 * 40 41 42 43 44 45 46 47
	 * 48 49 50 51 52 53 54 55 
	 * 56 57 58 59 60 61 62 63
	 */
	private void resetView() {
		// Put the whites into position
		siTac.put(0, 3); // 1 White shogun
		siTac.put(8, 5); // 2 White pawn
		siTac.put(16, 9); // 3 White pawn
		siTac.put(24, 13); // 4 White pawn
		siTac.put(32, 13); // 4 White pawn
		siTac.put(40, 9); // 3 White pawn
		siTac.put(48, 5); // 2 White pawn
		siTac.put(56, 1); // 1 White pawn
		// Put the black into position
		siTac.put(7, 0); // 1 Black pawn
		siTac.put(15, 4); // 2 Black pawn
		siTac.put(23, 8); // 3 Black pawn
		siTac.put(31, 12); // 4 Black pawn
		siTac.put(39, 12); // 4 Black pawn
		siTac.put(47, 8); // 3 Black pawn
		siTac.put(55, 4); // 2 Black pawn
		siTac.put(63, 2); // Black Shogun !!!
	}

	/**
	 * The view is drawing
	 */
	protected void onDraw(Canvas canvas) {
		for (int pos = 0; pos < 64; pos++) {
			Integer pawnState = siTac.get(pos);
			int[] realPos = null;
			if (posSelected != null && (possibleMoves.get(pos) || pos == posSelected)) { // Position
				// selected
				realPos = iconicToReal(pos);
				canvas.drawBitmap(getPossibleMoveBitmap(), realPos[0],
						realPos[1], null);
			}
			if (pawnState != null) { // If something on the position : Draw it
				realPos = iconicToReal(pos);
				canvas.drawBitmap(getPawnAsBitmap(pawnState), realPos[0],
						realPos[1], null);
			}
			if (pawnState != null && new Pawn(pawnState).isShogun()) { // If it's a shogun : add a crown
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
	private Bitmap getPawnAsBitmap(int pawnState) {
		Resources res = getResources();
		Pawn pawnPOJO = new Pawn(pawnState);
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
		posSelected = null;
		possibleMoves.clear();
	}

	/**
	 * Transform a real (x,y from sensor) location into an iconic location
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int realToIconic(float x, float y) {
		short deca = (short) Math.floor(x / ICON_WIDTH);
		short unite = (short) Math.floor(y / ICON_WIDTH);
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
	
	public Boolean getWinner(){
		return winnerWhite;
	}

}
