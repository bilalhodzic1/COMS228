package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 
 * @author Bilal Hodzic
 *
 */

public class VideoStore 
{
	protected SplayTree<Video> inventory;     // all the videos at the store
	
	// ------------
	// Constructors 
	// ------------
	
    /**
     * Default constructor sets inventory to an empty tree. 
     */
    public VideoStore()
    {
    	// no need to implement. 
    }
    
    
	/**
	 * Constructor accepts a video file to create its inventory.  Refer to Section 3.2 of  
	 * the project description for details regarding the format of a video file. 
	 * 
	 * Calls setUpInventory(). 
	 * 
	 * @param videoFile  no format checking on the file
	 * @throws FileNotFoundException
	 */
    public VideoStore(String videoFile) throws FileNotFoundException  
    {
    	inventory = new SplayTree<Video>();
    	setUpInventory(videoFile);
    }
    
    
   /**
     * Accepts a video file to initialize the splay tree inventory.  To be efficient, 
     * add videos to the inventory by calling the addBST() method, which does not splay. 
     * 
     * Refer to Section 3.2 for the format of video file. 
     * 
     * @param  videoFile  correctly formated if exists
     * @throws FileNotFoundException 
     */
    public void setUpInventory(String videoFile) throws FileNotFoundException
    {
    	File file = null;
    	try {
			file = new File(videoFile);
		}catch (Exception e ){
			System.out.println(e.getMessage());;
		}
    	Scanner scnr = new Scanner(file);
    	while(scnr.hasNext()){
    		String fullLine = scnr.nextLine();
    		String title = parseFilmName(fullLine);
    		int quantity = parseNumCopies(fullLine);
    		Video vid = new Video(title, quantity);
    		inventory.addBST(vid);
		}

    }
	
    
    // ------------------
    // Inventory Addition
    // ------------------
    
    /**
     * Find a Video object by film title. 
     * 
     * @param film
     * @return
     */
	public Video findVideo(String film) 
	{
		Video found = new Video(null);
		for(Video v : inventory){
			if (v.getFilm().equals(film)){
				found = v;
			}
		}
		return found;
	}


	/**
	 * Updates the splay tree inventory by adding a number of video copies of the film.  
	 * (Splaying is justified as new videos are more likely to be rented.) 
	 * 
	 * Calls the add() method of SplayTree to add the video object.  
	 * 
	 *     a) If true is returned, the film was not on the inventory before, and has been added.  
	 *     b) If false is returned, the film is already on the inventory. 
	 *     
	 * The root of the splay tree must store the corresponding Video object for the film. Update 
	 * the number of copies for the film.  
	 * 
	 * @param film  title of the film
	 * @param n     number of video copies 
	 */
	public void addVideo(String film, int n)  
	{
		Video toAdd = new Video(film, n);
		if (!inventory.add(toAdd)){
			Video exists = findVideo(film);
			exists.addNumCopies(n);
		}
	}
	

	/**
	 * Add one video copy of the film. 
	 * 
	 * @param film  title of the film
	 */
	public void addVideo(String film)
	{
		addVideo(film, 1);
	}
	

	/**
     * Update the splay trees inventory by adding videos.  Perform binary search additions by 
     * calling addBST() without splaying. 
     * 
     * The videoFile format is given in Section 3.2 of the project description. 
     * 
     * @param videoFile  correctly formated if exists 
     * @throws FileNotFoundException
     */
    public void bulkImport(String videoFile) throws FileNotFoundException 
    {
    	File file = null;
    	try {
			 file = new File(videoFile);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		Scanner scnr = new Scanner(file);
		while (scnr.hasNext()){
			String line = scnr.nextLine();
			String film = parseFilmName(line);
			int quantity = parseNumCopies(line);
			Video vid = new Video(film, quantity);
			if (!inventory.addBST(vid)){
				Video copy = findVideo(vid.getFilm());
				copy.addNumCopies(quantity);
			}

		}
    }

    
    // ----------------------------
    // Video Query, Rental & Return 
    // ----------------------------
    
	/**
	 * Search the splay tree inventory to determine if a video is available. 
	 * 
	 * @param  film
	 * @return true if available
	 */
	public boolean available(String film)
	{
		Video exist = findVideo(film);
		if (exist == null){
			return false;
		}else{
			if (exist.getNumAvailableCopies() > 0){
				return true;
			}else{
				return false;
			}
		}
	}

	
	
	/**
     * Update inventory. 
     * 
     * Search if the film is in inventory by calling findElement(new Video(film, 1)). 
     * 
     * If the film is not in inventory, prints the message "Film <film> is not 
     * in inventory", where <film> shall be replaced with the string that is the value 
     * of the parameter film.  If the film is in inventory with no copy left, prints
     * the message "Film <film> has been rented out".
     * 
     * If there is at least one available copy but n is greater than the number of 
     * such copies, rent all available copies. In this case, no AllCopiesRentedOutException
     * is thrown.  
     * 
     * @param film   
     * @param n 
     * @throws IllegalArgumentException      if n <= 0 or film == null or film.isEmpty()
	 * @throws FilmNotInInventoryException   if film is not in the inventory
	 * @throws AllCopiesRentedOutException   if there is zero available copy for the film.
	 */
	public void videoRent(String film, int n) throws IllegalArgumentException, FilmNotInInventoryException,  
									     			 AllCopiesRentedOutException 
	{
		try {
			if (n <= 0 || film == null || film.isEmpty()) {
				throw new IllegalArgumentException();
			}
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
		try {
			if (findVideo(film).getFilm() == null) {
				throw new FilmNotInInventoryException("Film " + film + " is not in inventory");
			}
		}catch (FilmNotInInventoryException e){
			System.out.println(e.getMessage());
		}
		try {
			if (!available(film)) {
				throw new AllCopiesRentedOutException("Film " + film + " is rented out");
			} else {
				Video vid = findVideo(film);
				vid.rentCopies(n);
			}
		}catch (AllCopiesRentedOutException e){
			System.out.println(e.getMessage());
		}

	}

	
	/**
	 * Update inventory.
	 * 
	 *    1. Calls videoRent() repeatedly for every video listed in the file.  
	 *    2. For each requested video, do the following: 
	 *       a) If it is not in inventory or is rented out, an exception will be 
	 *          thrown from videoRent().  Based on the exception, prints out the following 
	 *          message: "Film <film> is not in inventory" or "Film <film> 
	 *          has been rented out." In the message, <film> shall be replaced with 
	 *          the name of the video. 
	 *       b) Otherwise, update the video record in the inventory.
	 * 
	 * For details on handling of multiple exceptions and message printing, please read Section 3.4 
	 * of the project description. 
	 *       
	 * @param videoFile  correctly formatted if exists
	 * @throws FileNotFoundException
     * @throws IllegalArgumentException     if the number of copies of any film is <= 0
	 * @throws FilmNotInInventoryException  if any film from the videoFile is not in the inventory 
	 * @throws AllCopiesRentedOutException  if there is zero available copy for some film in videoFile
	 */
	public void bulkRent(String videoFile) throws FileNotFoundException, IllegalArgumentException, 
												  FilmNotInInventoryException, AllCopiesRentedOutException 
	{
		ArrayList<Exception> exceptions = new ArrayList<>();
		File file = null;
		try {
			file = new File(videoFile);
		}catch (Exception e){
			exceptions.add(e);
		}
		Scanner scnr = new Scanner(file);
		while (scnr.hasNext()){
			String line = scnr.nextLine();
			String title = parseFilmName(line);
			int quan = parseNumCopies(line);
			try{
				videoRent(title, quan);
			}catch (IllegalArgumentException e){
				exceptions.add(new IllegalArgumentException("Film "+ title + " has an invalid request"));
			}catch (FilmNotInInventoryException e){
				exceptions.add(new FilmNotInInventoryException("Film " + title + " is not in inventory"));
			}catch (AllCopiesRentedOutException e){
				exceptions.add(new AllCopiesRentedOutException("Film " + title + " has been rented out"));
			}
		}
		String message = "";
		for(Exception e : exceptions){
			message += e.getMessage() + "\n";
		}
		if (!exceptions.isEmpty()){
			System.out.println(message);
		}

	}

	
	/**
	 * Update inventory.
	 * 
	 * If n exceeds the number of rented video copies, accepts up to that number of rented copies
	 * while ignoring the extra copies. 
	 * 
	 * @param film
	 * @param n
	 * @throws IllegalArgumentException     if n <= 0 or film == null or film.isEmpty()
	 * @throws FilmNotInInventoryException  if film is not in the inventory
	 */
	public void videoReturn(String film, int n) throws IllegalArgumentException, FilmNotInInventoryException 
	{
		try {
			if (n <= 0 || film == null) {
				throw new IllegalArgumentException();
			}
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
		try {
			if(findVideo(film) == null){
				throw new FilmNotInInventoryException("Film " + film + " is not in inventory");
			}else{
				Video vid = findVideo(film);
				vid.returnCopies(n);
			}
		}catch (FilmNotInInventoryException e){
			System.out.println(e.getMessage());
		}

	}
	
	
	/**
	 * Update inventory. 
	 * 
	 * Handles excessive returned copies of a film in the same way as videoReturn() does.  See Section 
	 * 3.4 of the project description on how to handle multiple exceptions. 
	 * 
	 * @param videoFile
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException    if the number of return copies of any film is <= 0
	 * @throws FilmNotInInventoryException if a film from videoFile is not in inventory
	 */
	public void bulkReturn(String videoFile) throws FileNotFoundException, IllegalArgumentException,
													FilmNotInInventoryException												
	{
		ArrayList<Exception> exceptions = new ArrayList<>();
		File file = null;
		try {
			file = new File(videoFile);
		}catch (Exception e){
			exceptions.add(e);
		}
		Scanner scnr = new Scanner(file);
		while (scnr.hasNext()){
			String line = scnr.nextLine();
			String title = parseFilmName(line);
			int quan = parseNumCopies(line);
			try{
				videoReturn(title, quan);
			}catch (IllegalArgumentException e){
				exceptions.add(new IllegalArgumentException("Film "+ title + " has an invalid request"));
			}catch (FilmNotInInventoryException e){
				exceptions.add(new FilmNotInInventoryException("Film " + title + " is not in inventory"));
			}
		}
		String message = "";
		for(Exception e : exceptions){
			message += e.getMessage() + "\n";
		}
		if (!exceptions.isEmpty()){
			System.out.println(message);
		}

	}
		
	

	// ------------------------
	// Methods without Splaying
	// ------------------------
		
	/**
	 * Performs inorder traversal on the splay tree inventory to list all the videos by film 
	 * title, whether rented or not.  Below is a sample string if printed out: 
	 * 
	 * 
	 * Films in inventory: 
	 * 
	 * A Streetcar Named Desire (1) 
	 * Brokeback Mountain (1) 
	 * Forrest Gump (1)
	 * Psycho (1) 
	 * Singin' in the Rain (2)
	 * Slumdog Millionaire (5) 
	 * Taxi Driver (1) 
	 * The Godfather (1) 
	 * 
	 * 
	 * @return
	 */
	public String inventoryList()
	{
		Iterator<Video> iter = inventory.iterator();
		String toReturn = "Films in inventory \n";
		while(iter.hasNext()){
			Video vid = iter.next();
			toReturn += "\n" + vid.getFilm() + " (" + vid.getNumCopies() + ")";
		}
		return toReturn;
	}

	
	/**
	 * Calls rentedVideosList() and unrentedVideosList() sequentially.  For the string format, 
	 * see Transaction 5 in the sample simulation in Section 4 of the project description. 
	 *   
	 * @return 
	 */
	public String transactionsSummary()
	{
		return rentedVideosList() + "\n\n" + unrentedVideosList();
	}	
	
	/**
	 * Performs inorder traversal on the splay tree inventory.  Use a splay tree iterator.
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * Rented films: 
	 * 
	 * Brokeback Mountain (1)
	 * Forrest Gump (1) 
	 * Singin' in the Rain (2)
	 * The Godfather (1)
	 * 
	 * 
	 * @return
	 */
	public String rentedVideosList()
	{
		Iterator<Video> iter = inventory.iterator();
		String toReturn = "Rented films: \n";
		while (iter.hasNext()){
			Video vid = iter.next();
			if(vid.getNumAvailableCopies() < vid.getNumCopies()){
				toReturn += "\n" + vid.getFilm() + " (" + vid.getNumRentedCopies() + ")";
			}
		}
		return toReturn;
	}

	
	/**
	 * Performs inorder traversal on the splay tree inventory.  Use a splay tree iterator.
	 * Prints only the films that have unrented copies. 
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * 
	 * Films remaining in inventory:
	 * 
	 * A Streetcar Named Desire (1) 
	 * Forrest Gump (1)
	 * Psycho (1) 
	 * Slumdog Millionaire (4) 
	 * Taxi Driver (1) 
	 * 
	 * 
	 * @return
	 */
	public String unrentedVideosList()
	{
		Iterator<Video> iter = inventory.iterator();
		String toReturn = "Films remaining in inventory: \n";
		while(iter.hasNext()){
			Video vid = iter.next();
			if (vid.getNumAvailableCopies() > 0){
				toReturn += "\n" + vid.getFilm() + " (" + vid.getNumAvailableCopies() + ")";
			}
		}
		return toReturn;
	}	

	
	/**
	 * Parse the film name from an input line. 
	 * 
	 * @param line
	 * @return
	 */
	public static String parseFilmName(String line) 
	{
		String filmName = "";
		int count = 0;
		while(line.charAt(count) != '('){
			filmName += line.charAt(count);
			count++;
			if (count == line.length()){
				return filmName;
			}
		}
		int count2 = filmName.length() - 1;
		int ogCount2 = count2;
		int finalIndex = 0;
		while(filmName.charAt(count2) == ' '){
			finalIndex = count2;
			count2--;
		}
		if (count2 == ogCount2){
			return filmName;
		}else {
			filmName = filmName.substring(0, finalIndex);
		}
		return filmName;
	}
	
	
	/**
	 * Parse the number of copies from an input line. 
	 * 
	 * @param line
	 * @return
	 */
	public static int parseNumCopies(String line) 
	{
		int count = 0;
		int quan;
		while (line.charAt(count) != '('){
			count++;
			if (count == line.length()){
				return 1;
			}
		}
		count++;
		String number = "";
		while(line.charAt(count) != ')'){
			number += line.charAt(count);
			count++;
		}
		quan = Integer.valueOf(number);
		return quan;
	}
}
