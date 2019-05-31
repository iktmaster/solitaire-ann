import java.io.*; 

//********************************************************************
//  ViewerInterface.java              @version 1.02
//    Interface to a game state viewer.
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public interface ViewerInterface {
	public void open(int num) throws IOException;
	public void update(GameState gs);
	public void updateMove(GameState gs);
	public void close();
	public void remove();
}

