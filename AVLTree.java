
/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {
	
    private AVLNode min; //node with the minimal key of the tree
	private AVLNode max; //node with the maximal key of the tree
	private AVLNode root; //root of the tree
	
	/**
	* Creates and returns an object of type AVLTree initialized with default parameters
	* complexity: O(1)
	*/
	public AVLTree()
	{
		this(null,null,null);
	}
	
	/**
    * Creates and returns an object of type AVLTree initialized with the parameters inserted
    * complexity: O(1)
	*/
	public AVLTree(AVLNode root, AVLNode min, AVLNode max) 
	{
		if(root != null)
		{
			this.root = root;
			this.root.setParent(null);
		}
		else
		{
			this.root = new AVLNode();
		}
	  this.min = min;
	  this.max = max;
	}
	
    /**
    * public boolean empty()
    * complexity: O(1)
    * returns true if and only if the tree is empty
    */
	public boolean empty()
	{     
		if (!this.root.isRealNode())
		{
			return true;
		}
		return false; 
	}

    /**
    * public String search(int k)
    *
    * returns the info of an item with key k if it exists in the tree
    * otherwise, returns null
    * complexity: O(log(n))
    */
	public String search(int k)
	{
		AVLNode node = this.root;
		while(node.isRealNode())
		{
			if(node.getKey() == k)
			{
				return node.getValue();
			}
			else if (k < node.getKey())
			{
				node = (AVLNode) node.getLeft();
			}
			else
			{
				node = (AVLNode) node.getRight();
			}
		}
		return null;
	}

    /**
    * public int insert(int k, String i)
    *
    * inserts an item with key k and info i to the AVL tree.
    * the tree must remain valid (keep its invariants).
    * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
    * returns -1 if an item with key k already exists in the tree.
    * complexity: O(log(n))
    */
	public int insert(int k, String i) {
		
		AVLNode right = new AVLNode();
		AVLNode left = new AVLNode();
		if(this.empty()) //if the tree is empty make the node it's root
		{
			this.root = new AVLNode(i, k, left, right, null);
			left.setParent(this.root);
			right.setParent(this.root);
			this.max = this.root;
			this.min = this.root;
			return 0;
		}
		
		AVLNode insertAfter = treePosition(this.root, k); //find where to insert the new node
		if(insertAfter.getKey() == k)
		{
			return -1;
		}
		
		AVLNode nodeToInsert = new AVLNode(i, k, left, right, insertAfter); //create the new node
		left.setParent(nodeToInsert);
		right.setParent(nodeToInsert);
		if (k > this.max.getKey())  //update the min and max fields of the tree, if needed
		{
			this.max = nodeToInsert;
		}
		if (k < this.min.getKey())
		{
			this.min = nodeToInsert;
		}
		
		if(insertAfter.getKey() > k)  //insert the node in the right place
		{
			insertAfter.setLeft(nodeToInsert);
		}
		else
		{
			insertAfter.setRight(nodeToInsert);
		}
		insertAfter.size ++; //we added a node to this subtree so we need to increase its size
		
		if (insertAfter.getLeft().isRealNode() && insertAfter.getRight().isRealNode()) //if it was an unary node
		{                                                                             // update sizes till root and finish
			IAVLNode parent = insertAfter.getParent();
			updateSizesTillRoot(parent);
			return 0;  //no rebalancing operations were performed
		}
		
		promote(insertAfter); //the height of the node we inserted to should be increased
	    AVLNode z = (AVLNode) insertAfter.getParent(); 
	    //rebalancing!!
	    int rebalanceNum = rebalance(z) + 1; //+1 for the promote we just made                                   
	    return rebalanceNum; //calculated in the rebalance function
	}
	
	/**
	    * private int rebalance(IAVLNode z)
	    *
	    * rebalances the tree after an insertion, according to the
	    * problematic cases showed in class.
	    * updates the size fields of the nodes that need to be changed due to the insertion.
	    * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	    * complexity: O(log(n))
	    */
	private int rebalance(IAVLNode z) {
		int rebalanceNum = 0; 
		while((z != null) && (diff(z,z.getLeft()) == 0 || diff(z,z.getRight()) == 0))
		{
			if ((diff(z,z.getLeft()) == 0 && diff(z,z.getRight()) == 1) ||   //case 1
					(diff(z,z.getLeft()) == 1 && diff(z,z.getRight()) == 0)) 
			{
				promote(z);
				z.updateSize();
				rebalanceNum++;
			}
			else if (diff(z,z.getLeft()) == 0 && diff(z,z.getRight()) == 2 &&  //case 2
					diff(z.getLeft(),z.getLeft().getLeft()) == 1 && diff(z.getLeft(),z.getLeft().getRight()) == 2)
			{
				demote(z);
				rotateR(z,z.getLeft());
				rebalanceNum += 2;
			}
			else if (diff(z,z.getLeft()) == 2 && diff(z,z.getRight()) == 0 &&  //case 3
					diff(z.getRight(),z.getRight().getLeft()) == 2 && diff(z.getRight(),z.getRight().getRight()) == 1)
			{
				demote(z);
				rotateL(z,z.getRight());
				rebalanceNum += 2;
			}
			else if (diff(z,z.getLeft()) == 0 && diff(z,z.getRight()) == 2 &&  //case 4
					diff(z.getLeft(),z.getLeft().getLeft()) == 2 && diff(z.getLeft(),z.getLeft().getRight()) == 1)
			{
				demote(z); 
				demote(z.getLeft()); 
				promote(z.getLeft().getRight());
				rotateL(z.getLeft(),z.getLeft().getRight());
				rotateR(z,z.getLeft());
				rebalanceNum += 5;
			}
			else if (diff(z,z.getLeft()) == 2 && diff(z,z.getRight()) == 0 &&  //case 5
					diff(z.getRight(),z.getRight().getRight()) == 2 && diff(z.getRight(),z.getRight().getLeft()) == 1)
			{
				demote(z); 
				demote(z.getRight()); 
				promote(z.getRight().getLeft());
				rotateR(z.getRight(),z.getRight().getLeft());
				rotateL(z,z.getRight());
				rebalanceNum += 5;
			}
			z = (AVLNode) z.getParent(); //go up to check the parent
		}
		
		if (z != null) {
			updateSizesTillRoot(z); //maintaining the size fields of the nodes (some were updated during the rotations)
		}
		
		return rebalanceNum;
    }
	
	  /**
	   * The method receives 2 nodes, performs R rotation so that the tree is
	   * legal again.
	   * complexity: O(1)
	   */
	private void rotateR(IAVLNode z, IAVLNode x)
	{
		x.setParent(z.getParent());
		AVLNode tmp = (AVLNode) x.getRight();
		x.setRight(z);
		z.setParent(x);
		z.setLeft(tmp);
		tmp.setParent(z);
		if(x.getParent() != null)
		{
			if(x.getParent().getLeft() == z)
			{
				x.getParent().setLeft(x);
			}
			else
			{
				x.getParent().setRight(x);
			}
		}
		else 
		{
			this.root = (AVLNode) x;
		}
		z.updateSize();
		x.updateSize(); 
	}
	
	  /**
	   * The method receives 2 nodes, performs L rotation so that the tree is
	   * legal again.
	   * complexity: O(1)
	   */
	private void rotateL(IAVLNode z, IAVLNode x)
	{
		x.setParent(z.getParent());
		AVLNode tmp = (AVLNode) x.getLeft();
		x.setLeft(z);
		z.setParent(x);
		z.setRight(tmp);
		tmp.setParent(z);
		if(x.getParent() != null)
		{
			if(x.getParent().getLeft() == z)
			{
				x.getParent().setLeft(x);
			}
			else
			{
				x.getParent().setRight(x);
			}
		}
		else 
		{
			this.root = (AVLNode) x;
		}
		z.updateSize();  
		x.updateSize();   
	}
	/**
	 * The method sets the height of z to be height+1
	 * complexity: O(1)
	**/
	private static void promote(IAVLNode z)
	{
		z.setHeight(z.getHeight()+1);
	}
	
	/**
	 * The method sets the height of z to be height-1
	 * complexity: O(1)
	**/
	private static void demote(IAVLNode z)
	{
		z.setHeight(z.getHeight()-1);
	}
	
	/**
	 * The method calculates the difference between the heights of 2 nodes,
	 * when the first node is the parent of the second node
	 * complexity: O(1)
	**/
	private static int diff(IAVLNode parent, IAVLNode son)
	{
		return parent.getHeight()-son.getHeight();
	}
	
	/**
	 * The method updates the sizes of the subtree of the nodes
	 * in the route from the given node till the root of the tree 
	 * complexity: O(log(n))
	**/
	private void updateSizesTillRoot(IAVLNode node) {
		while (node != null) 
		{
			node.updateSize();
			node = node.getParent();
		} 	
	}
	  
	/**
	   * if exists a node with key k, then the method returns the node
	   * else, the method finds and returns an insertion place for k
	   * complexity: O(log(n))
	   */
	public AVLNode treePosition(AVLNode node, int k)
	{
		AVLNode pos = new AVLNode();
		while(node.isRealNode()) 
		{
			pos = node;
			if(k == node.getKey())
			{
				return node;
			}
			else if(k < node.getKey())
			{
				node = node.left;
			}
			else
			{
				node = node.right;
			}
		}
		return pos;
	}
	
	/**
	 * public int delete(int k)
     *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 * complexity: O(log(n))
	 */
	public int delete(int k)
	{
		AVLNode nodeToDelete = treePosition(this.root, k); //find the node we want to delete
		if(nodeToDelete.getKey() != k) //if it's not found return -1
		{
			return -1;
		}
		if (this.size() == 1) { //if it's the only node in the tree, make the tree empty
			this.root = new AVLNode();
			this.min = null;
			this.max = null;
			return 0;
		}
		
		//update min and max fields of the tree if necessary:
		if (this.max == nodeToDelete)  
		{
			this.max = nodeToDelete.predecessor();
		}
		if (this.min == nodeToDelete)
		{
			this.min = nodeToDelete.successor();
		}
		
		AVLNode successor;
		AVLNode z = (AVLNode) nodeToDelete.getParent(); 
		
		//now for the deletion (3 cases):
		if (!nodeToDelete.getLeft().isRealNode() && !nodeToDelete.getRight().isRealNode()) //is a leaf
		{ 
			replaceByVirtual(nodeToDelete);	
		}
		else if (!nodeToDelete.getLeft().isRealNode() || !nodeToDelete.getRight().isRealNode()) //is an unary node
		{ 
			if (nodeToDelete == this.root) //edge case, where the unary node is the root
			{
				AVLNode realChild;
				if (nodeToDelete.getLeft().isRealNode()) 
				   {
					   realChild = (AVLNode) nodeToDelete.getLeft();
				   }
				   else 
				   {
					   realChild = (AVLNode) nodeToDelete.getRight();
				   }
				this.root = realChild;
				realChild.setParent(null);
			}
			else //for the general case:
			{
			bypass(nodeToDelete); //deleting by bypassing the node to delete
			}
		}
		
		else  //is a binary node (has 2 sons)
		{ 
			successor = nodeToDelete.successor(); //find the successor
			z = (AVLNode) successor.getParent(); //z is the parent of the successor, we will need it for the rebalancing
			//we will delete now the successor:
			if (!successor.getLeft().isRealNode() && !successor.getRight().isRealNode()) { //is a leaf
				replace(nodeToDelete,successor);
				replaceByVirtual(successor);
			}
			else if (!successor.getLeft().isRealNode() || !successor.getRight().isRealNode()) { //is an unary node
				replace(nodeToDelete,successor);
				bypass(successor);
			}
		}
		
		int rebalanceNum = 0; //number of rebalancing operations made due to the deletion 
		
		//now we rebalance! (according to the cases shown in class - 7 cases including the symmetric ones)
		while((z != null) && ((diff(z,z.getLeft()) == 3 || diff(z,z.getRight()) == 3) || 
				(diff(z,z.getLeft()) == 2 && diff(z,z.getRight()) == 2))) //these are the cases we need to perform a rebalance action of some sort.
			                                                              //we stop at the root or when the height differences are legal.
		{
			if ((diff(z,z.getLeft()) == 2 && diff(z,z.getRight()) == 2)) //case 1
			{
				demote(z);
				z.updateSize(); 
				rebalanceNum++;
			}
			else if (diff(z,z.getLeft()) == 3 && diff(z,z.getRight()) == 1 &&  //case 2
					diff(z.getRight(),z.getRight().getRight()) == 1 && diff(z.getRight(),z.getRight().getLeft()) == 1)
			{
				demote(z);
				promote(z.getRight());
				rotateL(z,z.getRight());
				z = (AVLNode) z.getParent();
				rebalanceNum += 3;
			}
			else if (diff(z,z.getLeft()) == 3 && diff(z,z.getRight()) == 1 &&  //case 3
					diff(z.getRight(),z.getRight().getRight()) == 1 && diff(z.getRight(),z.getRight().getLeft()) == 2)
			{
				demote(z);
				demote(z);
				rotateL(z,z.getRight());
				z = (AVLNode) z.getParent();
				rebalanceNum += 3;
			}
			else if (diff(z,z.getLeft()) == 3 && diff(z,z.getRight()) == 1 &&  //case 4
					diff(z.getRight(),z.getRight().getRight()) == 2 && diff(z.getRight(),z.getRight().getLeft()) == 1)
			{
				demote(z);
				demote(z);
				promote(z.getRight().getLeft());
				demote(z.getRight());
				rotateR(z.getRight(),z.getRight().getLeft());
				rotateL(z,z.getRight());
				z = (AVLNode) z.getParent();
				rebalanceNum += 6;
			}
			else if (diff(z,z.getLeft()) == 1 && diff(z,z.getRight()) == 3 &&  //case 5
					diff(z.getLeft(),z.getLeft().getLeft()) == 1 && diff(z.getLeft(),z.getLeft().getRight()) == 1)
			{
				demote(z); 
				promote(z.getLeft());
				rotateR(z,z.getLeft());
				z = (AVLNode) z.getParent();
				rebalanceNum += 3;
			}
			else if (diff(z,z.getLeft()) == 1 && diff(z,z.getRight()) == 3 &&  //case 6
					diff(z.getLeft(),z.getLeft().getLeft()) == 1 && diff(z.getLeft(),z.getLeft().getRight()) == 2)
			{
				demote(z); 
				demote(z);
				rotateR(z,z.getLeft());
				z = (AVLNode) z.getParent();
				rebalanceNum += 3;
			}
			else if (diff(z,z.getLeft()) == 1 && diff(z,z.getRight()) == 3 &&  //case 7
					diff(z.getLeft(),z.getLeft().getLeft()) == 2 && diff(z.getLeft(),z.getLeft().getRight()) == 1)
			{
				demote(z); 
				demote(z);
				demote(z.getLeft());
				promote(z.getLeft().getRight());
				rotateL(z.getLeft(),z.getLeft().getRight());
				rotateR(z,z.getLeft());
				z = (AVLNode) z.getParent();
				rebalanceNum += 6;
			}
			
			z = (AVLNode) z.getParent(); //continue to check the parent of z
		}
		
		if (z != null) {
			updateSizesTillRoot(z); //update sizes for the nodes we didn't visit during the rebalance
		}
		
		return rebalanceNum;
	}
	
	
	/** 
	 * The method replaces the node x and the node s (s is the successor of x)
	 * by switching their keys and values (info)
	 * @pre x and y are real nodes
	 * complexity: O(1)
	 */
	private void replace(AVLNode x, AVLNode s) { 
	   String xValue = x.getValue();
	   int xKey = x.getKey();
	   x.setKey(s.getKey());
	   x.setValue(x.getValue());
	   s.setKey(xKey);
	   s.setValue(xValue);
	}
   
	/**
	 * The method deletes the node y from the tree by bypassing it
	 * @pre y is an unary node
	 * complexity: O(1)
	 */
    private void bypass(AVLNode y) {
	   AVLNode realChild; //y has only one child, we need to know who it is
	   if (y.getLeft().isRealNode()) 
	   {
		   realChild = (AVLNode) y.getLeft();
	   }
	   else 
	   {
		   realChild = (AVLNode) y.getRight();
	   }
	   // make the bypass by connecting y's parent to it's real child:
	   if (y.getParent().getLeft() == y)
	   {
		   y.getParent().setLeft(realChild);
		   realChild.setParent(y.getParent());
	   }
	   else
	   {
		   y.getParent().setRight(realChild);
		   realChild.setParent(y.getParent());
	   }
   }
   
    /**
	 * The method deletes the node x from the tree by replacing it with a virtual node
	 * @pre x is a leaf
	 * complexity: O(1)
	 */ 
   private void replaceByVirtual(AVLNode x) {
	   AVLNode virtual = new AVLNode();
	   if(x.getParent().getLeft() == x)
	   {
		   x.getParent().setLeft(virtual);
	   }
	   else
	   {
		   x.getParent().setRight(virtual);
	   }
	   virtual.setParent(x.getParent());
	}
   
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    * complexity: O(1)
    */
	public String min()
	{
		if(this.empty())
		{
			return null;
		}
		return this.min.getValue();
	}	

    /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    * complexity: O(1)
    */
	public String max()
	{
		if(this.empty())
		{
			return null;  
		}
		return this.max.getValue(); 
	}

    /**
    * public int[] keysToArray()
    *
    * Returns a sorted array which contains all keys in the tree,
    * or an empty array if the tree is empty, using the recursive
    * method keysToArray(AVLNode node,int[] arr, int i)
    * complexity: O(n)
    */
	public int[] keysToArray()
	{
		int[] arr = new int[this.size()]; 
		if(!this.empty())
		{
			 keysToArray(this.root,arr,0);
		}
        return arr;              
	}
	
	/** 
	 * The method fills arr with the keys in the tree in ascending order
	 * complexity: O(n)
	 */
	public int keysToArray(AVLNode node,int[] arr, int i)
	{
		if(node.left.isRealNode())
		{
			i = keysToArray(node.left, arr, i);
		}
		arr[i] = node.getKey();
		i++;
		if(node.right.isRealNode())
		{
			i = keysToArray(node.right, arr, i);
		}
		return i;
	}	
	

	/**
	* public String[] infoToArray()
	*
    * Returns an array which contains all info in the tree,
    * sorted by their respective keys, or an empty array if the tree is empty,
    * using the recursive function infoToArray(AVLNode node,String[] arr, int i)
    * complexity: O(n)
	*/
	public String[] infoToArray()
	{
		String[] arr = new String[this.size()];
		if(!this.empty())
		{
			 infoToArray(this.root,arr,0);
		}
        return arr;              
	}
	
	/**
	   * The method fills arr with the info of the nodes such that the arr is sorted
	   * in ascending order according to the keys.
	   * complexity: O(n)
	   */
	public int infoToArray(AVLNode node,String[] arr, int i)
		{
			if(node.left.isRealNode())
			{
				i = infoToArray(node.left, arr, i);
			}
			arr[i] = node.getValue();
			i++;
			if(node.right.isRealNode())
			{
				i =infoToArray(node.right, arr, i);
			}
			return i;	
		}
	
   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * @pre: none
    * @post: none
    * complexity: O(1)
    */
	public int size()
	{
		return this.root.getSize();
	}
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * @pre: none
    * @post: none
    * complexity: O(1)
    */
	public IAVLNode getRoot()
	{
		if(this.empty())
		{
			return null;  
		}
		return this.root;
	}
	
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	* 
	* @pre: search(x) != null
    * @post: none
    * complexity: O(log(n))
    */   
	public AVLTree[] split(int x) 
	{
		IAVLNode node = treePosition(this.root,x); //find the node we want to split at
		
		AVLNode smallerChild = (AVLNode) node.getLeft(); 
		AVLTree smaller = new AVLTree(smallerChild,null,null); //the tree of smaller keys than x
		
		AVLNode biggerChild = (AVLNode) node.getRight();
		AVLTree bigger = new AVLTree(biggerChild,null,null); //the tree of bigger nodes than x
		
		AVLNode tmpMin = new AVLNode(); //temporal min and max nodes to send as parameters to tree constructor
		AVLNode tmpMax = new AVLNode(); //because we don't want it to be null for edge cases
		
		if (node == this.root) //already devided to smaller and bigger, the split is done
		{
			//uptade min and max fields:
			smaller.min = smaller.root.min();
			smaller.max = smaller.root.max();
			bigger.min = bigger.root.min();
			bigger.max = bigger.root.max(); 
			return new AVLTree[] {smaller,bigger};
		}
		
		IAVLNode tmp = node.getParent();
		node.setParent(null); //detach node from it's parent
		node = tmp; //now the node is its parent
		
		while (node != null) //stop when we get to the root's parent who is null
		{
			IAVLNode y = node.getParent();
			node.setParent(null); //detach node from it's parent
			
			if (node.getKey() > x) //join the node and its right subtree with bigger
			{
				AVLNode root = (AVLNode) (node.getRight());
				root.setParent(null);
				AVLTree t = new AVLTree(root,tmpMin,tmpMax);
				bigger.join(node,t);
			}
			else //node.getKey() < x  //join the node and its left subtree with smaller
			{
				AVLNode root = (AVLNode) (node.getLeft());
				root.setParent(null);
				AVLTree t = new AVLTree(root,tmpMin,tmpMax);
				smaller.join(node,t);
			}
			node = y; //y is the parent of the node before the detachment
		}
		
		//uptade min and max fields:
		if (smaller.empty()) 
		{
			smaller.min = null;
			smaller.max = null;
		}
		else //find the min and max in the tree
		{
			smaller.min = smaller.root.min();
			smaller.max = smaller.root.max();
		}
		if (bigger.empty()) 
		{
			bigger.min = null;
			bigger.max = null; 
	    }
		else //find the min and max in the tree
		{
			bigger.min = bigger.root.min();
			bigger.max = bigger.root.max(); 
		}
		return new AVLTree[] {smaller,bigger}; 
	}
	
   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (rank difference between the tree and t)
    * @pre: keys(x,t) < keys() or keys(x,t) > keys()
    * @post: none
    * complexity: O(log(n))
    */   
	public int join(IAVLNode x, AVLTree t)
	{
		//we'll refer this as t1, and t as t2
		int counter; //the cost of join
		if(this.empty())
		{
			if(!t.empty()) //t1 empty and t2 full
			{
				counter = t.getRoot().getHeight()+1;
				t.insert(x.getKey(), x.getValue());
				this.root = (AVLNode) t.getRoot();
				this.min = t.min;
				this.max = t.max;
			}
			else //t1 empty and t2 empty
			{
				counter = 1; 
				AVLNode right = new AVLNode();
				AVLNode left = new AVLNode();
				this.root = new AVLNode (x.getValue(),x.getKey(),left,right,null);
				this.root.updateSize();
				this.root.setHeight(0);
				this.min = this.root;
				this.max = this.root;
			}
		}
		else 
		{
			if(t.empty()) //t1 full and t2 empty
			{
				counter = this.root.getHeight()+1;
				this.insert(x.getKey(), x.getValue());
			}
			else //t1 full and t2 full
			{
				counter = Math.abs(this.root.getHeight() - t.getRoot().getHeight())+1;
				if(this.getRoot().getHeight() == t.getRoot().getHeight()) //trees have the same height
				{
					if(this.getRoot().getKey() < x.getKey()) //this is on the left
					{
						this.max = t.max;
						x.update(this.getRoot(),t.getRoot());	
					}
					else //this is on the right
					{
						this.min = t.min;
						x.update(t.getRoot(), this.getRoot());
					}
					x.updateSize();
					x.setHeight(this.getRoot().getHeight()+1);
					this.root = (AVLNode) x;
					x.setParent(null);
				}
				else //the trees full but not with the same heights
				{
					if(this.getRoot().getKey() < x.getKey())  //this is on the left
					{
						this.max = t.max;
						if(this.getRoot().getHeight() < t.getRoot().getHeight())  //this is shorter
						{
							x.setHeight(this.getRoot().getHeight()+1);
							AVLNode b = findPlaceLeft(t,this.getRoot().getHeight());
							if (b == t.getRoot()) 
							{
								x.update(this.getRoot(),b);
								this.root = (AVLNode) x;
								x.setParent(null);
								x.updateSize();
							}
							else 
							{
								x.setParent(b.getParent());
								x.getParent().setLeft(x);
								x.update(this.getRoot(), b);
								x.updateSize();
								this.root = (AVLNode) t.getRoot();
								AVLNode z = (AVLNode) x.getParent();
								AVLNode y = (AVLNode) z.getParent();
								rebalanceForJoin(z,y);
							}
						}
						else //this is higher
						{
							x.setHeight(t.getRoot().getHeight()+1);
							AVLNode a = findPlaceRight(this,t.getRoot().getHeight());
							if (a == this.getRoot()) 
							{
								x.update(a, t.getRoot());
								this.root = (AVLNode) x;
								x.setParent(null);
								x.updateSize();
							}
							else 
							{
								x.setParent(a.getParent());
								x.getParent().setRight(x);
								x.update(a, t.getRoot());
								x.updateSize();
								AVLNode z = (AVLNode) x.getParent();
								AVLNode y = (AVLNode) z.getParent();
								rebalanceForJoin(z,y);
							}
						}
					}
					else //this.getRoot().getKey() > x.getKey()  //this is on the right
					{
						this.min = t.min;
						if(this.getRoot().getHeight() < t.getRoot().getHeight())  //this is shorter
						{
							x.setHeight(this.getRoot().getHeight()+1);
							AVLNode a = findPlaceRight(t,this.getRoot().getHeight());
							if (a == t.getRoot()) 
							{
								x.update(a,this.getRoot());
								this.root = (AVLNode) x;
								x.setParent(null);
								x.updateSize();
							}
							else
							{
								x.setParent(a.getParent());
								x.getParent().setRight(x);
								x.update(a, this.getRoot());
								x.updateSize();
								this.root = (AVLNode) t.getRoot();
								AVLNode z = (AVLNode) x.getParent();
								AVLNode y = (AVLNode) z.getParent();
								rebalanceForJoin(z,y);
							}
						}
						else   //this is higher
						{
							x.setHeight(t.getRoot().getHeight()+1);
							AVLNode b = findPlaceLeft(this,t.getRoot().getHeight());
							if (b == this.getRoot()) 
							{
								x.update(t.getRoot(),b);
								this.root = (AVLNode) x;
								x.setParent(null);
								x.updateSize();
							}
							else 
							{
								x.setParent(b.getParent());
								x.getParent().setLeft(x);
								x.update(t.getRoot(), b);
								x.updateSize();
								AVLNode z = (AVLNode) x.getParent();
								AVLNode y = (AVLNode) z.getParent();
								rebalanceForJoin(z,y);
							}
						}
					}
				}
			}
		}
		return counter; 
	}

	/**
	 * the method makes the rebalancing process after a join operation was made.
	 * it gets two nodes - a node and its parent, checks a unique problematic case, fixes it if needed
	 * and then sends the right node from the two to the rebalance function.
	 * complexity: O(log(n))
	 */
	private void rebalanceForJoin(AVLNode z, AVLNode y) 
	{
		//2 cases unique for join:
		boolean flag = false; //needed to decide from which node we start the rebalancing of Insert
		if (diff(z,z.getLeft()) == 2 && diff(z,z.getRight()) == 0 && 
				diff(z.getRight(),z.getRight().getLeft()) == 1 && diff(z.getRight(),z.getRight().getRight()) == 1)
		{
			promote(z.getRight());
			rotateL(z,z.getRight());
			flag = true;
		} 
		//the symmetric case:
		else if (diff(z,z.getLeft()) == 0 && diff(z,z.getRight()) == 2 && 
				diff(z.getLeft(),z.getLeft().getLeft()) == 1 && diff(z.getLeft(),z.getLeft().getRight()) == 1)
		{
			promote(z.getLeft());
			rotateR(z,z.getLeft());
			flag = true;
		}
		if (flag) {
			rebalance(y); //if we made a fix here, start rebalancing from the upper node (y)
		}
		else
		{
			rebalance(z); //else, start rebalancing from the current node (z)
		}	
	}

	/**
	  * the method get a tree and return the node with 
	  * height k or k-1(the first found)
	  * complexity: O(log(n))
	  */
	private static AVLNode findPlaceLeft(AVLTree tree, int k)
	{
		AVLNode node= (AVLNode) tree.getRoot();
		if (!node.getLeft().isRealNode()) {
			return node;
		}
		while(node.getHeight() > k)
			{
				node = (AVLNode) node.getLeft();
			}
		return node;
	}
	
	/**
	  * the method get a tree and return the node with 
	  * height k or k-1(the first that it finds)
	  * complexity: O(log(n))
	  */
	private static AVLNode findPlaceRight(AVLTree tree, int k)
	{
		AVLNode node= (AVLNode) tree.getRoot();
		if (!node.getRight().isRealNode()) {
			return node;
		}
		while(node.getHeight() > k)
			{
				node = (AVLNode) node.getRight();
			}
		return node;
	}
	
	
	
 
	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtual node return -1)
		public String getValue(); //returns node's value [info] (for virtual node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // returns the height of the node (-1 for virtual nodes)
    	//public AVLNode min(); // returns the node with the minimal key in the subtree of the node
		//public AVLNode max(); // returns the node with the maximal key in the subtree of the node
		public void updateSize(); //updates the size of the subtree of the node
		public int getSize();  //returns the size of the subtree of the node
		public void update(IAVLNode node1, IAVLNode node2); //sets sons of the object and sets sons' parent as that object
	}

	
	
	
   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   */
  public class AVLNode implements IAVLNode{
	  private String info; //value of the node
	  private int key; //key of the node
	  private int height; //height of the node, height == rank
	  private AVLNode left; //left son of the node
	  private AVLNode right; //right son of the node
	  private AVLNode parent; //parent of the node
	  private int size; //size of the subtree, that it's root is the node
	  
	  /**
	   * The method returns an object of type AVLNode 
	   * initialized with values inserted
	   * complexity: O(1)
	   */
	  public AVLNode(String info, int key, AVLNode left, AVLNode right, AVLNode parent)//##to check if we need sum and if it is ok to get it
	  	{
	  		this.info = info; 
	  		this.key = key;
	  		this.left = left;
	  		this.right = right;
	  		this.parent = parent;
	  		if(this.key == -1)
	  		{
	  			this.size = 0;
	  			this.height = -1;
	  		}
	  		else
	  		{
	  			this.size = left.size+right.size+1;
	  			this.height = Math.max(left.height, right.height)+1; //change to 0 if needed
	  		}	
	  	}  
	  
	  /**
	   * The method returns an object of type AVLNode 
	   * initialized with default values
	   *  complexity: O(1)
	   */
	  public AVLNode()
		  {
			  this(null,-1,null,null,null);
		  }
	  
	  /**
		  * public void setKey(int k)
		  * sets the key of the node to be k
		  *  complexity: O(1)
		  */ 
		public void setKey(int k)
		{
			this.key = k; 
		}
		
	  /**
	    * public int getKey()
	    * returns node's key,	
	    * for virtual node returns -1
	    *  complexity: O(1)
	    */ 
		public int getKey()  
		{
			return this.key; 
		}
		
		/**
		  * public void setValue(String s)
		  * sets the value of the node [info] to be s
		  *  complexity: O(1)
		  */ 
		public void setValue(String s)
		{
			this.info = s; 
		}
		
		/**
		  * public String getValue()
		  * returns node's value [info], 
		  * for virtual node returns null
		  *  complexity: O(1)
		  */ 
		public String getValue() 
		{
			return this.info; 
		}
		
		/**
		  * public void setLeft(IAVLNode node)
		  * sets left child.
		  *  complexity: O(1)
		  */ 
		public void setLeft(IAVLNode node)
		{
			this.left = (AVLNode) node; 
		}
		
		/**
		  * public IAVLNode getLeft()
		  * returns left child,
		  * if there is no left child returns null.
		  *  complexity: O(1)
		  */ 
		public IAVLNode getLeft()
		{
			return this.left; 
		}
		
		/**
		  * public void setRight(IAVLNode node)
		  * sets right child
		  *  complexity: O(1)
		  */ 
		public void setRight(IAVLNode node)
		{
			this.right = (AVLNode) node;
		}
		
		/**
		  * public IAVLNode getRight()
		  * returns right child,
		  * if there is no right child returns null.
		  *  complexity: O(1)
		  */ 
		public IAVLNode getRight()
		{
			return this.right; 
		}
		
		/**
		  * public void setParent(IAVLNode node)
		  * sets parent.
		  *  complexity: O(1)
		  */ 
		public void setParent(IAVLNode node)
		{
			this.parent = (AVLNode) node; 
		}
		
		/**
		  * public IAVLNode getParent()
		  * returns the parent, 
		  * if there is no parent returns null.
		  *  complexity: O(1)
		  */ 
		public IAVLNode getParent()
		{
			return this.parent; 
		}
		
		/**
		  * public boolean isRealNode()
		  * returns true if this is a non-virtual AVL node.
		  * else, returns false.
		  *  complexity: O(1)
		  */ 
		public boolean isRealNode()
		{
			if(this.height == -1)
			{
				return false;
			}
			return true; 
		}
		
		/**
		  * public void setHeight(int height)
		  * sets the height of the node
		  *  complexity: O(1)
		  */ 
		public void setHeight(int height)
		{
			this.height = height; 
		}
		
		/**
		  * public int getHeight()
		  * Returns the height of the node,
		  * for virtual node returns -1
		  *  complexity: O(1)
		  */ 
		public int getHeight()
		{
			return this.height; 
		}
		
		/**
		  * public void updateSize()
		  * updates the size of the subtree of the node 
		  * (0 for virtual nodes)
		  *  complexity: O(1)
		  */ 
		public void updateSize()
		{
			if (this.isRealNode()) {
				this.size = this.left.size + this.right.size + 1;
			}
			else {
				this.size = 0;
			}
		}
		
		/**
		  * public int getSize()
		  * Returns the size of the subtree of the node,
		  * for virtual node returns 0
		  *  complexity: O(1)
		  */ 
		public int getSize() 
		{
			return this.size; 
		}
		
		 /**
	     * @pre this.isRealNode()
		 * @post The method returns the node with the minimal key in the subtree of this
		 * complexity: O(log(n))
		 */
		public AVLNode min()
		{
			AVLNode node= this;
			while(node.getLeft().isRealNode())
			{
				node = (AVLNode) node.getLeft();
			}
			return node;
		}
		
		 /**
	     * @pre this.isRealNode()
		 * @post The method returns the node with the maximal key in the subtree of this
		 * complexity: O(log(n))
		 */
		public AVLNode max()
		{
			AVLNode node= this;
			while(node.getRight().isRealNode())
			{
				node = (AVLNode) node.getRight();
			}
			return node;
		}
		
		/**
		 * @pre this.isRealNode()
		 * returns the successor of this.
		 * complexity: O(log(n))
		 */
		public  AVLNode successor()
		{
			AVLNode x = this;
			if(x.getRight().isRealNode())
			{
				//return x.getRight().min();
				return ((AVLNode) x.getRight()).min();
			}
			AVLNode parent = (AVLNode) x.getParent();
			while(parent != null && x == parent.getRight())
			{
				x = parent;
				parent = (AVLNode) parent.getParent();
			}
			return parent;
		}
		
		/**
		 * @pre this.isRealNode()
		 * The method returns the predecessor of this
		 * complexity: O(log(n))
		 */
		public AVLNode predecessor()
		{
			AVLNode x = this;
			if(x.getLeft().isRealNode())
			{
				//return  x.getLeft().max();
				return  ((AVLNode)x.getLeft()).max();
			}
			AVLNode parent = (AVLNode) x.getParent();
			while(parent != null && x == getParent().getLeft())
			{
				x = parent;
				parent = (AVLNode) parent.getParent();
			}
			return parent;
		}
		
		/**
		 * The method sets sons and sons' parent
		 * complexity: O(1)
		 */
		public void update(IAVLNode left, IAVLNode right)
		{
			this.setLeft(left);
			this.setRight(right);
			right.setParent(this);
			left.setParent(this);
		}
  }

}
  

