package org.spiritsofts.shogun.board;

import org.spiritsofts.shogun.Shogun;
import org.spiritsofts.shogun.board.view.BoardView;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.MotionEvent;

public class BoardDisplay extends Activity {

	BoardView board;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		board = new BoardView(getApplicationContext());
		if (!getIntent().getExtras().getBoolean(Shogun.LOAD)) { // if new game,
																// clear the old
																// one
			loadGame(board);
		}
		setContentView(board);
	}

	private void loadGame(BoardView board) {
		// Get the load data somewhere
		SparseIntArray[] load = null;
		// board.resumeSavedView(load);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadGame(board);
	}

	private void saveState() {
		SparseIntArray[] savedGame = board.getSaveInfo();
		// TODO : Save the actual data
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);

	}

}
