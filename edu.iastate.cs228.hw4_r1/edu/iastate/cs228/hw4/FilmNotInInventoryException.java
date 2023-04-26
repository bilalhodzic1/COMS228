package edu.iastate.cs228.hw4;

/**
 * 
 * @author Yan-Bin Jia
 * 
 */

/**
 * Expression thrown for a video not in the inventory.
 */
public class FilmNotInInventoryException extends Exception
{
  public FilmNotInInventoryException()
  {
    super();
  }
  
  public FilmNotInInventoryException(String msg)
  {
    super(msg);
  }
}

