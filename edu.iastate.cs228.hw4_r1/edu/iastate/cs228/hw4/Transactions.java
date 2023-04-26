package edu.iastate.cs228.hw4;


import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 *  
 * @author Bilal Hodzic
 *
 */

/**
 * 
 * The Transactions class simulates video transactions at a video store. 
 *
 */
public class Transactions {

	/**
	 * The main method generates a simulation of rental and return activities.
	 *
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, FilmNotInInventoryException, AllCopiesRentedOutException {
		VideoStore store = new VideoStore("edu/iastate/cs228/hw4/videoList1.txt");
		Scanner scnr = new Scanner(System.in);
		System.out.println("Transactions at a video store");
		System.out.println("keys: 1 (rent)    2 (bulk rent)\n" +
				"      3 (return)  4 (bulk return\n" +
				"      5 (summary) 6 (exit)\n");
		int usernum = 0;
		while (usernum != 6) {
			System.out.print("\nTransaction: ");
			usernum = scnr.nextInt();
			if (usernum == 1) {
				System.out.print("Film to rent: ");
				scnr.nextLine();
				String line = scnr.nextLine();
				String name = VideoStore.parseFilmName(line);
				int quan = VideoStore.parseNumCopies(line);
				store.videoRent(name, quan);
			} else if (usernum == 2) {
				System.out.print("Video file (rent): ");
				scnr.nextLine();
				store.bulkRent(scnr.nextLine());
			} else if (usernum == 3) {
				System.out.print("Film to return: ");
				scnr.nextLine();
				String line = scnr.nextLine();
				String name = VideoStore.parseFilmName(line);
				int quan = VideoStore.parseNumCopies(line);
				store.videoReturn(name, quan);
			} else if (usernum == 4) {
				System.out.print("Video file (return): ");
				scnr.nextLine();
				store.bulkReturn(scnr.nextLine());
			} else if (usernum == 5) {
				System.out.println(store.transactionsSummary());
			} else if (usernum < 1 || usernum > 6) {
				System.out.print("try new number");
			}
		}
	}
}

