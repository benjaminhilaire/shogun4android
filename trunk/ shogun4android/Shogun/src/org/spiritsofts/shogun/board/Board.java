package org.spiritsofts.shogun.board;

import org.spiritsofts.shogun.R;
import org.spiritsofts.shogun.Shogun;
import org.spiritsofts.shogun.board.ai.ArtificialIntelligence;
import org.spiritsofts.shogun.board.ai.basic.LevelOne;
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
	private int m_AILevel;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extra = getIntent().getExtras();
		if (extra.getBoolean(Shogun.LOAD)) {
			board = getLastGame();
		} else {
			board = new BoardView(getApplicationContext());
		}
		int level = extra.getInt(Shogun.IA_LEVEL);
		if (level > 0){
			onePlayer = true;
			m_AILevel = level;
		} else {
			onePlayer = false;
		}
		setContentView(board);
	}

	private BoardView getLastGame() {
		return new BoardView(getApplicationContext());
		// TODO : Get somewhere the last saved game
	}
	
	private void loadGame(int[] positions, boolean white){
		board.resumeSavedView(positions, white);
	}
	
	private void loadLastGame(){
		// TODO : get somewhere the last game
		loadGame(null,true);
		// TODO Get the IA Level
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
		//loadLastGame();
	}

	private void saveGame() {
		int[] saveGame = board.getSaveInfo();
		boolean whiteTurn = board.getTurn();
		// TODO : Save somewhere
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN :
			int position = board.realToIconic(event.getX(),event.getY()-MAGICAL_CORRECTION);
			if (onePlayer){
				if (whiteTurn){
					if (humanTurn(position)){
						AIturn();
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

	private void AIturn() {
		switchTurn();
		ArtificialIntelligence lvl = new LevelOne();
		int[] toMove = lvl.calculateNextMove(board.getSaveInfo());
		board.moveAIPawn(toMove[0],toMove[1]);
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
