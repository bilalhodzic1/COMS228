package edu.iastate.cs228.hw4;


import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 *
 * @author Bilal Hodzic
 *
 */


/**
 *
 * This class implements a splay tree.  Add any helper methods or implementation details
 * you'd like to include.
 *
 */


public class SplayTree<E extends Comparable<? super E>> extends AbstractSet<E>
{
	protected Node root;
	protected int size;

	public class Node  // made public for grading purpose
	{
		public E data;
		public Node left;
		public Node parent;
		public Node right;

		public Node(E data) {
			this.data = data;
		}

		@Override
		public Node clone() {
			return new Node(data);
		}
	}


	/**
	 * Default constructor constructs an empty tree.
	 */
	public SplayTree()
	{
		size = 0;
	}


	/**
	 * Needs to call addBST() later on to complete tree construction.
	 */
	public SplayTree(E data)
	{
		root = new Node(data);
		size = 1;
	}


	/**
	 * Copies over an existing splay tree. The entire tree structure must be copied.
	 * No splaying. Calls cloneTreeRec().
	 *
	 * @param tree
	 */
	public SplayTree(SplayTree<E> tree)
	{
		root = cloneTreeRec(tree.root);
		size = tree.size();
	}


	/**
	 * Recursive method called by the constructor above.
	 *
	 * @param subTree
	 * @return
	 */
	public Node cloneTreeRec(Node subTree)
	{
		if (subTree == null){
			return null;
		}
		Node newRoot = subTree.clone();
		newRoot.left = cloneTreeRec(subTree.left);
		if (newRoot.left != null){
			newRoot.left.parent = newRoot;
		}
		newRoot.right = cloneTreeRec(subTree.right);
		if (newRoot.right != null){
			newRoot.right.parent = newRoot;
		}
		return newRoot;
	}


	/**
	 * This function is here for grading purpose. It is not a good programming practice.
	 *
	 * @return element stored at the tree root
	 */
	public E getRoot()
	{
		return root.data;
	}


	@Override
	public int size()
	{
		return size;
	}


	/**
	 * Clear the splay tree.
	 */
	@Override
	public void clear()
	{
		root = null;
		size = 0;
	}


	// ----------
	// BST method
	// ----------

	/**
	 * Adds an element to the tree without splaying.  The method carries out a binary search tree
	 * addition.  It is used for initializing a splay tree.
	 *
	 * Calls link().
	 *
	 * @param data
	 * @return true  if addition takes place
	 *         false otherwise (i.e., data is in the tree already)
	 */
	public boolean addBST(E data) {
		if (size == 0) {
			root = new Node(data);
			size++;
			return true;
		} else {
			Node n = findEntry(data);
			if (n == null) {
				Node j = lastFound(data);
				if (j.data.compareTo(data) > 0) {
					j.left = new Node(data);
					link(j, j.left);
					size++;
					return true;
				} else {
					j.right = new Node(data);
					link(j, j.right);
					size++;
					return true;
				}
			} else {
				return false;
			}
		}
	}


	// ------------------
	// Splay tree methods
	// ------------------

	/**
	 * Inserts an element into the splay tree. In case the element was not contained, this
	 * creates a new node and splays the tree at the new node. If the element exists in the
	 * tree already, it splays at the node containing the element.
	 *
	 * Calls link().
	 *
	 * @param  data  element to be inserted
	 * @return true  if addition takes place
	 *         false otherwise (i.e., data is in the tree already)
	 */
	@Override
	public boolean add(E data)
	{
		Node n = findEntry(data);
		if (n == null){
			Node j = lastFound(data);
			if (j.data.compareTo(data) > 0){
				j.left = new Node(data);
				link(j, j.left);
				splay(j.left);
				size++;
				return true;
			}else{
				j.right = new Node(data);
				link(j, j.right);
				splay(j.right);
				size++;
				return true;
			}
		}else{
			splay(n);
			return false;
		}
	}
	/**
	 * Determines last found node if the node is not found with findEntry. Used by add remove and contains for splaying purposes
	 * @param data element to find last node
	 * @return the last encountered node
	 *
	 */
	public Node lastFound(E data){
		Node found = root;
		while (true) {
			int comp = found.data.compareTo(data);
			if (comp < 0) {
				if (found.right == null) {
					return found;
				} else {
					found = found.right;
				}
			} else {
				if (found.left == null) {
					return found;
				} else {
					found = found.left;
				}
			}
		}
	}


	/**
	 * Determines whether the tree contains an element.  Splays at the node that stores the
	 * element.  If the element is not found, splays at the last node on the search path.
	 *
	 * @param  data  element to be determined whether to exist in the tree
	 * @return true  if the element is contained in the tree
	 *         false otherwise
	 */
	public boolean contains(E data)
	{
		Node found = findEntry(data);
		if (findEntry(data) == null){
			found = lastFound(data);
			splay(found);
		}
		splay(found);
		return true;
	}


	/**
	 * Finds the node that stores the data and splays at it.
	 *
	 * @param data
	 */
	public void splay(E data)
	{
		contains(data);
	}


	/**
	 * Removes the node that stores an element.  Splays at its parent node after removal
	 * (No splay if the removed node was the root.) If the node was not found, the last node
	 * encountered on the search path is splayed to the root.
	 *
	 * Calls unlink().
	 *
	 * @param  data  element to be removed from the tree
	 * @return true  if the object is removed
	 *         false if it was not contained in the tree
	 */
	public boolean remove(E data)
	{
		if (size == 0){
			return false;
		}
		Node node = findEntry(data);
		if (node == null){
			Node n = lastFound(data);
			System.out.println(n.data);
			splay(n);
			return false;
		}else{
			if (node == root){
				unlink(node);
				size--;
				return true;
			}else {
				Node parent = node.parent;
				unlink(node);
				splay(parent);
				size--;
				return true;
			}

		}
	}


	/**
	 * This method finds an element stored in the splay tree that is equal to data as decided
	 * by the compareTo() method of the class E.  This is useful for retrieving the value of
	 * a pair <key, value> stored at some node knowing the key, via a call with a pair
	 * <key, ?> where ? can be any object of E.
	 *
	 * Calls findEntry(). Splays at the node containing the element or the last node on the
	 * search path.
	 *
	 * @param  data
	 * @return element such that element.compareTo(data) == 0
	 */
	public E findElement(E data)
	{
		Node node = findEntry(data);
		splay(node);
		return null;
	}


	/**
	 * Finds the node that stores an element. It is called by methods such as contains(), add(), remove(),
	 * and findElement().
	 *
	 * No splay at the found node.
	 *
	 * @param  data  element to be searched for
	 * @return node  if found or the last node on the search path otherwise
	 *         null  if size == 0.
	 */
	public Node findEntry(E data)
	{
		Node current = root;
		while (current != null) {
			int comp = current.data.compareTo(data);
			if (comp == 0) {
				return current;
			}
			else if (comp > 0) {
				current = current.left;
			}
			else {
				current = current.right;
			}
		}
		return null;
	}


	/**
	 * Join the two subtrees T1 and T2 rooted at root1 and root2 into one.  It is
	 * called by remove().
	 *
	 * Precondition: All elements in T1 are less than those in T2.
	 *
	 * Access the largest element in T1, and splay at the node to make it the root of T1.
	 * Make T2 the right subtree of T1.  The method is called by remove().
	 *
	 * @param root1  root of the subtree T1
	 * @param root2  root of the subtree T2
	 * @return the root of the joined subtree
	 */
	public Node join(Node root1, Node root2)
	{
		if (root1 == null) {
			return root2;
		}

		if (root2 == null) {
			return root1;
		}
		Node max = root2;
		while(max.right != null){
			max = max.right;
		}
		splay(max);
		max.right = root2;
		root2.parent = max;
		return max;
	}


	/**
	 * Splay at the current node.  This consists of a sequence of zig, zigZig, or zigZag
	 * operations until the current node is moved to the root of the tree.
	 *
	 * @param current  node to splay
	 */
	protected void splay(Node current)
	{
		while(current.parent != null){
			if (current.parent.parent == null){
				zig(current);
			}else if (current == current.parent.left && current.parent == current.parent.parent.left){
				zigZig(current);
			}else if (current == current.parent.right && current.parent == current.parent.parent.right){
				zigZig(current);
			}else{
				zigZag(current);
			}
		}
	}


	/**
	 * This method performs the zig operation on a node. Calls leftRotate()
	 * or rightRotate().
	 *
	 * @param current  node to perform the zig operation on
	 */
	protected void zig(Node current)
	{
		if (current == current.parent.left){
			rightRotate(current.parent);
		}else{
			leftRotate(current.parent);
		}
	}


	/**
	 * This method performs the zig-zig operation on a node. Calls leftRotate()
	 * or rightRotate().
	 *
	 * @param current  node to perform the zig-zig operation on
	 */
	protected void zigZig(Node current)
	{
		if (current == current.parent.left){
			rightRotate(current.parent.parent);
			rightRotate(current.parent);
		}else{
			leftRotate(current.parent.parent);
			leftRotate(current.parent);
		}
	}


	/**
	 * This method performs the zig-zag operation on a node. Calls leftRotate()
	 * and rightRotate().
	 *
	 * @param current  node to perform the zig-zag operation on
	 */
	protected void zigZag(Node current)
	{
		if (current == current.parent.right){
			leftRotate(current.parent);
			rightRotate(current.parent);
		}else{
			rightRotate(current.parent);
			leftRotate(current.parent);
		}
	}


	/**
	 * Carries out a left rotation at a node such that after the rotation
	 * its former parent becomes its left child.
	 *
	 * Calls link().
	 *
	 * @param current
	 */
	public void leftRotate(Node current)
	{
		Node old = current.right;
		current.right = old.left;
		if (old.left != null) {
			old.left.parent = current;
		}
		old.parent = current.parent;
		if (current.parent == null) {
			this.root = old;
		} else if (current == current.parent.left) {
			current.parent.left = old;
		} else {
			current.parent.right = old;
		}
		old.left = current;
		current.parent = old;
	}


	/**
	 * Carries out a right rotation at a node such that after the rotation
	 * its former parent becomes its right child.
	 *
	 * Calls link().
	 *
	 * @param current
	 */
	public void rightRotate(Node current)
	{
		Node old = current.left;
		current.left = old.right;
		if (old.right != null) {
			old.right.parent = current;
		}
		old.parent = current.parent;
		if (current.parent == null) {
			this.root = old;
		} else if (current == current.parent.right) {
			current.parent.right = old;
		} else {
			current.parent.left = old;
		}
		old.right = current;
		current.parent = old;
	}


	/**
	 * Establish the parent-child relationship between two nodes.
	 *
	 * Called by addBST(), add(), leftRotate(), and rightRotate().
	 *
	 * @param parent
	 * @param child
	 */
	private void link(Node parent, Node child)
	{
		if (child != null){
			child.parent = parent;
			if (parent == null){
				if (root == null){
					root = child;
				}
			}else if (child.data.compareTo(parent.data) < 0){
				parent.left = child;
			}else if (child.data.compareTo(parent.data) > 0){
				parent.right = child;
			}
		}
	}


	/**
	 * Removes a node n by replacing the subtree rooted at n with the join of the node's
	 * two subtrees.
	 *
	 * Called by remove().
	 *
	 * @param n
	 */
	private void unlink(Node n)
	{

		if (n.left == null && n.right == null){
			if (n == root){
				root = null;
			}else if (n.parent.left == n){
				n.parent.left = null;
			}else if (n.parent.right == n){
				n.parent.right = null;
			}
			return;
		}
		if (n.left != null){
			n.left.parent = null;
		}
		if (n.right != null){
			n.right.parent = null;
		}

		if (n == root){
			root = join(n.left, n.right);
		}else{
			link(n.parent, join(n.left, n.right));
		}

	}


	/**
	 * Perform BST removal of a node.
	 *
	 * Called by the iterator method remove().
	 * @param n
	 */
	public void unlinkBST(Node n)
	{
		Node parent = n.parent;
		if (n.left == null && n.right == null) {
			if (parent.right.equals(n)) {
				parent.right = null;
			} else {
				parent.left = null;
			}
		} else if (n.left == null) {
			if (parent.right.equals(n)) {
				parent.right = n.right;
			} else {
				parent.left = n.right;
			}
		} else if (n.right == null) {
			if (parent.right.equals(n)) {
				parent.right = n.left;
			} else {
				parent.left = n.left;
			}
		} else {
			Node toUnlink = successor(n);
			n.data = toUnlink.data;
			unlink(toUnlink);
		}
	}


	/**
	 * Called by unlink() and the iterator method next().
	 *
	 * @param n
	 * @return successor of n
	 */
	public Node successor(Node n)
	{
		if (n == null) {
			return null;
		}
		else if (n.right != null) {
			Node current = n.right;
			while (current.left != null)
			{
				current = current.left;
			}
			return current;
		}
		else {
			Node current = n.parent;
			Node child = n;
			while (current != null && current.right == child)
			{
				child = current;
				current = current.parent;
			}
			// either current is null, or child is left child of current
			return current;
		}
	}


	@Override
	public Iterator<E> iterator()
	{
		return new SplayTreeIterator();
	}


	/**
	 * Write the splay tree according to the format specified in Section 2.2 of the project
	 * description.
	 *
	 * Calls toStringRec().
	 *
	 */
	@Override
	public String toString()
	{
		return  toStringRec(root, 0);
	}


	private String toStringRec(Node n, int depth)
	{
		String output = "";
		for (int i = 0; i < depth; i++){
			output += "    ";
		}
		if (n == null){
			return output + n + "\n";
		}
		output += n.data +"\n";
		if (n.left == null && n.right == null){
			return output;
		}
		output += toStringRec(n.left, depth + 1);
		output += toStringRec(n.right, depth + 1);
		return output;
	}


	/**
	 *
	 * Iterator implementation for this splay tree.  The elements are returned in
	 * ascending order according to their natural ordering.  The methods hasNext()
	 * and next() are exactly the same as those for a binary search tree --- no
	 * splaying at any node as the cursor moves.  The method remove() method
	 * should not splay.
	 */
	private class SplayTreeIterator implements Iterator<E>
	{
		Node cursor;
		Node pending;

		public SplayTreeIterator()
		{
			cursor = root;
			while (cursor.left != null) {
				cursor = cursor.left;
			}
		}

		@Override
		public boolean hasNext()
		{
			return cursor != null;
		}

		@Override
		public E next()
		{
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			pending = cursor;
			cursor = successor(cursor);
			return pending.data;
		}

		/**
		 * This method will join the left and right subtrees of the node being removed,
		 * but will not perform a splay operation.
		 *
		 * Calls unlinkBST().
		 *
		 */
		@Override
		public void remove()
		{
			if (pending == null) {
				throw new IllegalStateException();
			}
			if (pending.left != null && pending.right != null)
			{
				cursor = pending;
			}
			unlinkBST(pending);
			pending = null;
		}
	}
}
