import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

//********************************************************************
//  ViewerText.java              @version 1.01
//    Text-based game state viewer (to file).
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public class ViewerText implements ViewerInterface {

	//-----------------------------------------------------------------
	//  Fields
	//-----------------------------------------------------------------
	PrintWriter printer;
	String myName;

	//-----------------------------------------------------------------
	//  Open
	//-----------------------------------------------------------------
	public void open (int number) throws IOException {
		String name = "firstmove/output" + number + ".txt";
		System.out.println(name);
		//File file = new File(name, "UTF-8");
		//file.createNewFile();
		//FileOutputStream oFile = new FileOutputStream(name, false); 
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(name, false), StandardCharsets.UTF_8);
		myName = name;
		printer = new PrintWriter(osw);	
	}

	//-----------------------------------------------------------------
	//  Close
	//-----------------------------------------------------------------
	public void close () {
		printer.close();	
	}

	//-----------------------------------------------------------------
	//  Update
	//-----------------------------------------------------------------
	public void updateMove (GameState gs) {
		//print(gs.p1 + " ");
		//print(gs.p2 + " ");
		//print(gs.p3 + "");
		//print(gs.card1 + " ");
		//print(gs.card2 + "");
		print(gs.getMove() + "");
		print("\r\n");
	}
	
	//-----------------------------------------------------------------
	//  Update
	//-----------------------------------------------------------------
	public void update (GameState gs) {		

		/*
		
		print("\r\n");
		print("\r\n");
		
		// First row: top of deck, waste, foundations
		print(gs.deck());
		print(gs.waste());
		print("   ");
		for (int i = 0; i < 4; i++) {
			print(gs.found(i));
		}
		print("\r\n");
		
		// Find max length of tables
		int maxTable = 0;
		for (int i = 0; i < 7; i++) {
			int size = gs.table(i).size();
			if (size > maxTable) maxTable = size;
		}
		
		// Print a row for each
		for (int i = 0; i < maxTable; i++) {
			for (int j = 0; j < 7; j++) {
				if (i < gs.table(j).size())
					print(gs.table(j).get(i));
				else 
					print("   ");
			}
			print("\r\n");
		}
		print("\r\n");
		
		
		*/
		
		
		print2(gs.deck());
		print2(gs.waste());
		for (int i = 0; i < 4; i++) {
			print2(gs.found(i));
		}
		
		//print("\r\n");
		
		int maxNed = 13;
		
		// Print a row for each
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < maxNed+j; i++) {
				if (i < gs.table(j).size())
					print(gs.table(j).get(i));
				else 
					print("0 ");
			}
			//print("\r\n");
		}
		
		
	}

	void print2(Pile pile){
		if (pile.isEmpty()) {
			print("0 ");
		}
		else {
			print(pile.getTopCard());
		}
	}
	
	//-----------------------------------------------------------------
	//  Print top of pile
	//-----------------------------------------------------------------
	void print (Pile pile) {
		if (pile.isEmpty()) {
			print("-- ");
		}
		else {
			print(pile.getTopCard());
		}
	}

	//-----------------------------------------------------------------
	//  Print one card
	//-----------------------------------------------------------------
	void print (Card card) {
		print(card.isFaceUp() ? card.toString() + " " : "-1 ");
	}
	
	//-----------------------------------------------------------------
	//  Print shortcut
	//-----------------------------------------------------------------
	void print (String s) {
		printer.print(s);
   }

	@Override
	public void remove() {
		File file = new File(myName); 
        
        if(file.delete()) 
        { 
            System.out.println("File deleted successfully"); 
        } 
        else
        { 
            System.out.println("Failed to delete the file"); 
        } 
	}
}

