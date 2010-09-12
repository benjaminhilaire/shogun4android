package org.spiritsofts.shogun.board;

import org.spiritsofts.shogun.board.view.BoardView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BoardDisplay extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		if (!getIntent().getExtras().getBoolean("RESUME")){ // if new game, clear the old one
			clearGame();
		}
		BoardView board = new BoardView(getApplicationContext());
		
		setContentView(board);
	}

	private void clearGame() {
		Log.i("DisplayBoard","Clearing the game for a new game !");
		// TODO : Not yet implemented
	}

}
