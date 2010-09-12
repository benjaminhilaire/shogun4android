package org.spiritsofts.shogun.board.view;

import org.spiritsofts.shogun.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {

	private static final int ICON_HEIGHT = 40;
	private static final int ICON_WIDTH = 40;

	private SparseIntArray white;
	private SparseIntArray black;
	private SparseIntArray positions;
	private int pawnSelected = 0;
	private int posSelected = -1;
	private int whiteShogun = 0;
	private int blackShogun = 63;
	private boolean whiteTurn = true;
	private SparseBooleanArray possibleMoves;

	private int realToIconic(float x, float y) {
		int deca = (int) Math.floor(x / ICON_WIDTH);
		int unite = (int) Math.floor(y / ICON_WIDTH);
		return 8 * unite + deca;
	}

	private int[] iconicToReal(int number) {
		int collumn = number % 8;
		int line = (int) Math.floor(number / 8);
		return new int[] { collumn * ICON_HEIGHT, line * ICON_WIDTH };
	}

	public BoardView(Context context) {
		super(context);
		resetView();
	}

	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			int iconicPosition = realToIconic(event.getX(), event.getY());
			int pawn = positions.get(iconicPosition);
			if (pawn > 1) { // If selection
				if (pawnSelected == pawn) { // Deselection
					clearSelected();
				} else {
					if (possibleMoves.get(iconicPosition)) { // PAWN TAKEN
						movePawn(pawnSelected, iconicPosition, posSelected);
						if (!whiteTurn && iconicPosition == blackShogun) {
							gameOver(true);
						}
						if (whiteTurn && iconicPosition == whiteShogun) {
							gameOver(false);
						}
					} else { // NEW SELECTION
						addPossibleMoves(pawn, iconicPosition);
					}
				}
			} else { // If move
				if (pawnSelected != 0 && possibleMoves.get(iconicPosition)) {
					movePawn(pawnSelected, iconicPosition, posSelected);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			invalidate();
			break;
		}
		return true;
	}

	private void clearSelected() {
		pawnSelected = 0;
		posSelected = -1;
		possibleMoves.clear();
	}

	private void gameOver(boolean winner) {
		Log.i("SHOGUN", "Winner is white : " + winner);
	}

	private void movePawn(int toMove, int iconicPosition, int oldPos) {
		Pawn pawn = new Pawn(toMove, iconicPosition, black, white);
		if (pawn.isWhite() == whiteTurn) {
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
			whiteTurn = !whiteTurn;
			clearSelected();
		}
	}

	/**
	 * "Lighten" all possible moves by putting its ID to 1
	 * 
	 * @param pawn
	 */
	private void addPossibleMoves(int pawnId, int iconicPosition) {
		Pawn pawn = new Pawn(pawnId, iconicPosition, black, white);
		if (pawn.isWhite() == whiteTurn) {
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
	 * 32 33 34 35 36 37 38 39 
	 * 40 41 42 43 44 45 46 47
	 * 48 49 50 51 52 53 54 55
	 * 56 57 58 59 60 61 62 63
	 */
	private void resetView() {
		white = createPreFilledArray();
		black = createPreFilledArray();
		positions = new SparseIntArray(64);
		possibleMoves = new SparseBooleanArray(64);
		// Put the whites into position
		positions.put(0, 10);
		positions.put(8, 11);
		positions.put(16, 12);
		positions.put(24, 13); // White Shogun !!!
		positions.put(32, 14);
		positions.put(40, 15);
		positions.put(48, 16);
		positions.put(56, 17);
		// Put the black into position
		positions.put(7, 21);
		positions.put(15, 22);
		positions.put(23, 23);
		positions.put(31, 24);
		positions.put(39, 25); // Black Shogun !!!
		positions.put(47, 26);
		positions.put(55, 27);
		positions.put(63, 20);
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

	protected void onDraw(Canvas canvas) {
		Log.i("SHOGUN", "Full draw");
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

	private Bitmap getPossibleMoveBitmap() {
		Drawable drawable = getResources().getDrawable(R.drawable.selected);
		return populateDrawable(drawable);
	}

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

	private Bitmap populateDrawable(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(ICON_WIDTH, ICON_HEIGHT,
				Bitmap.Config.ARGB_8888);
		drawable.setBounds(0, 0, ICON_WIDTH, ICON_HEIGHT);
		Canvas canvas = new Canvas(bitmap);
		drawable.draw(canvas);
		return bitmap;
	}

}
