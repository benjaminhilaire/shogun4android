package org.spiritsofts.shogun.board;

import org.spiritsofts.shogun.R;
import org.spiritsofts.shogun.Shogun;
import org.spiritsofts.shogun.board.view.BoardView;
import org.spiritsofts.shogun.howto.Rules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class Board extends Activity {

	private BoardView board;
	private boolean onePlayer = false;
	private boolean whiteTurn = true;

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
		//SparseIntArray[] load = null;
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
		//SparseIntArray[] savedGame = board.getSaveInfo();
		// TODO : Save the actual data
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN :
			int position = board.realToIconic(event.getX(),event.getY()-50);
			if (onePlayer){
				if (whiteTurn){
					humanTurn(position);
				} else {
					IAturn();
				}
			} else {
				humanTurn(position);
			}
			break;
		case MotionEvent.ACTION_UP:
			board.invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}

	private void IAturn() {
		whiteTurn = !whiteTurn;
	}
	
	private void humanTurn(int position){
		boolean whiteTurnToPlay = whiteTurn;
		if (board.isPawnMoving(position)){
			if (board.moveHumanPawn(position,whiteTurnToPlay)){
				if (whiteTurnToPlay && board.isBlackShogun(position)) {
                     Toast.makeText(getApplicationContext(),R.string.blue_won,
                                     Toast.LENGTH_LONG).show();
                     finish();
				 } else if (!whiteTurnToPlay && board.isWhiteShogun(position)) {
                         Toast.makeText(getApplicationContext(),R.string.red_won,
                                         Toast.LENGTH_LONG).show();
                        finish();
                 } else {
                	 whiteTurn = !whiteTurnToPlay;
                 }
			} else {
				if (board.moveHumanPawn(position,whiteTurnToPlay)){
					whiteTurn = !whiteTurnToPlay;
				}
			}
		}
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
