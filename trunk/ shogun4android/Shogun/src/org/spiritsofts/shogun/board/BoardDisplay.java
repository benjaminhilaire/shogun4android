package org.spiritsofts.shogun.board;

import org.spiritsofts.shogun.R;
import org.spiritsofts.shogun.Shogun;
import org.spiritsofts.shogun.board.rules.Rules;
import org.spiritsofts.shogun.board.view.BoardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class BoardDisplay extends Activity {

	BoardView board;
	private boolean onePlayer = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		board = new BoardView(getApplicationContext());
		if (!getIntent().getExtras().getBoolean(Shogun.LOAD)) { // clear the old
			// // one
			loadGame(board);
		}
		onePlayer = getIntent().getExtras().getBoolean(Shogun.ONEPLAYER);
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
		if (board.isGameOver()) {
			finish();
		}
		if (onePlayer) {
			board.waitForIA();
			calculateNextMove();
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			board.invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}

	private void calculateNextMove() {
		board.stopWaitForIA();
		board.movePawn(20, 62, 63);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, R.string.rules_title);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent i = new Intent(this,Rules.class);
			startActivityForResult(i,0);
			return true;
		}
		 return super.onMenuItemSelected(featureId, item);
	}

}
