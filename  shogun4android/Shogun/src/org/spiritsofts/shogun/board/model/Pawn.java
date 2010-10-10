package org.spiritsofts.shogun.board.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn {

	private int m_location; // Iconic location

	private Boolean m_white; // CANNOT BE CHANGED

	private Boolean m_shogun; // CANNOT BE CHANGED
	
	private int m_nbMove; // Number of move for the pawn
	
	private int m_state;
	
	private boolean mProtection = true;

	public Pawn(Integer state,int location) {
		m_state = state;
		m_location = location;
	}
	
	public Pawn(Integer state) {
		m_state = state;
	}
	
	public boolean isWhite(){
		if (m_white == null){
			m_white = ((m_state % 2) == 1);
		}
		return m_white;
	}
	
	public int getNbMove() {
		if (m_nbMove == 0){
			m_nbMove = (int) Math.floor(m_state / 4)+1;
		}
		return m_nbMove;
	}

	public int getLocation() {
		return m_location;
	}

	public void setLocation(int location) {
		this.m_location = location;
	}

	public boolean isShogun() {
		if (m_shogun == null){
			m_shogun = ((m_state / 2)%2 == 1);
		}
		return m_shogun;
	}

	public List<Integer> getPossibleMove(int[] siTac,boolean protection){
		int nbMove = getNbMove();
		int location = m_location;
		int leftDistance = m_location%8;
		mProtection = protection;
		List<Integer> result = new ArrayList<Integer>(4);
		if (leftDistance-nbMove >= 0 && isPossibleMove(location,location - nbMove,true,true,siTac)){ // LEFT
			result.add(location - nbMove);		
		}
		if (leftDistance+nbMove < 8 && isPossibleMove(location,location + nbMove,true,false,siTac)){ // RIGHT
			result.add(location + nbMove);
		}
		int bigMove = 8*nbMove;
		if ((location-bigMove) >= 0 && (isPossibleMove(location,location - bigMove,false,true,siTac))){ // TOP 
			result.add(location - bigMove);
		}
		if ((location+bigMove) < 64 && (isPossibleMove(location,location + bigMove,false,false,siTac))){ // BOTTOM
			result.add(location + bigMove);
		}
		return result;
	}
	
	public boolean isPossibleMove(int origin,int target,boolean horizontal,boolean topleft,int[] siTac){
		boolean success=true;
		int delta;
		if (horizontal){
			delta = 1;
		} else {
			delta = 8;
		}
		if (topleft){
			delta = -delta;
		}
		int it=origin;
		while(it != target && success){
			it = it+delta;
			if (siTac[it] >= 0 && it!= target){
				success = false;
			}
		}
		if (success && isNotAFriend(siTac[target])){
			return true;
		}
		return false;
	}
	
	private boolean isNotAFriend(int otherState){
		if (otherState >= 0 && mProtection){
			if (m_white){
				return (otherState % 2 == 0);
			} else {
				return (otherState % 2 == 1);
			}
		}
		return true;
	}
	
	public int getNextState(){
		int next = getNbMove();
		if (next >= 4){
			next = 0;
		}
		next*=4;
		if (isShogun()){
			next+=2;
		}
		if (isWhite()){
			next++;
		}
		return next;
	}

}
