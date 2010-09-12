package org.spiritsofts.shogun;

import org.spiritsofts.shogun.board.BoardDisplay;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Shogun extends ListActivity {
	/** Called when the activity is first created. */
	
    private static final int ACTIVITY_BOARD=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		String[] menuList = getResources().getStringArray(R.array.menu_array);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main, menuList));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i("Shogun", "Click on a menu option");
		switch (position) {
		case 0: // NEW GAME
			startGame(false);
		break;
		case 1: // RESUME GAME
			startGame(true);
			break;
		default:
			Toast.makeText(getApplicationContext(), "NOT YET IMPLEMENTED",
					Toast.LENGTH_SHORT).show();
			Log.e("Shogun", "NOT YET IMPLEMENTED MENU OPTION");
			break;
		}
	}

	private void startGame(boolean resumed) {
		Intent i = new Intent(this,BoardDisplay.class);
		i.putExtra("RESUME",resumed);
		startActivityForResult(i, ACTIVITY_BOARD);
	}
	

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
        switch (requestCode) {
		case ACTIVITY_BOARD: // NEW GAME
			fillData();
		break;
        }
    }
	
}