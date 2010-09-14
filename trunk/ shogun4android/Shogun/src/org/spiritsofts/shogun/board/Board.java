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

	private static final int MAGICAL_CORRECTION = 50;
	private BoardView board;
	private boolean onePlayer = false;
	private boolean whiteTurn = true;
	private int m_IALevel;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		board = new BoardView(getApplicationContext());
		Bundle extra = getIntent().getExtras();
		if (!extra.getBoolean(Shogun.LOAD)) {
			loadGame();
		}
		int level = extra.getInt(Shogun.IA_LEVEL);
		if (level > 0){
			onePlayer = true;
			m_IALevel = level;
		} else {
			onePlayer = false;
		}
		setContentView(board);
	}

	private void loadGame() {
		// board.resumeSavedView(load);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveGame();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveGame();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadGame();
	}

	private void saveGame() {
		//SparseArray<Integer> saveGame = board.getSaveInfo();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN :
			int position = board.realToIconic(event.getX(),event.getY()-MAGICAL_CORRECTION);
			if (onePlayer){
				if (whiteTurn){
					if (humanTurn(position)){
						IAturn();
					}
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
		switchTurn();
	}
	
	private boolean humanTurn(int position){
		boolean whiteTurnToPlay = whiteTurn;
		if (board.isPawnMoving(position)){
			if (board.moveHumanPawn(position,whiteTurnToPlay)){
				if (board.getWinner() != null){
					int message = 0; 
					if (board.getWinner()){
						message = R.string.blue_won;
					} else {
						message = R.string.red_won;
					}
					 Toast.makeText(getApplicationContext(),message,
                             Toast.LENGTH_LONG).show();
					 finish();
				} else {
                	 switchTurn();
                	 return true;
                 }
			}
		}
		return false;
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
	
	/**
	 * Switch the Game turn
	 */
	private void switchTurn(){
		whiteTurn = !whiteTurn;
		board.switchTurn();
	}

}
