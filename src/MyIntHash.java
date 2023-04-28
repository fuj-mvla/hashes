import java.util.LinkedList;

// TODO: Auto-generated Javadoc
/**
 * The Class MyIntHash.
 */
public class MyIntHash {
	
	/**
	 * The Enum MODE.
	 */
	enum MODE {Linear, Quadratic,  LinkedList,  Cuckoo};
	
	/** The Constant INITIAL_SIZE. */
	private final static int INITIAL_SIZE = 31;
	
	/** The mode of operation. */
	private MODE mode = MODE.Linear;
	
	private final int MAX_QP_OFFSET = 2<<15;
	private  int 	max_QP_LOOP;
	
	/** The physical table size. */
	private int tableSize;
	
	/** The size of the hash - the number of elements in the hash. */
	private int size;
	
	/** The load factor. */
	private double load_factor; 
	
	/** The hash table 1. */
	private int[] hashTable1;
	
	
	// The following variables will be defined but not used until later in the project..
	/** The hash table 2. */
	private int[] hashTable2;
	
	/** The hash table LL. */
	private LinkedList<Integer>[] hashTableLL;
	
	
	/**
	 * Instantiates a new my int hash. For Part1 JUnit Testing, the load_factor will be set to 1.0
	 *
	 * @param mode the mode
	 * @param load_factor the load factor
	 */
	public MyIntHash(MODE mode, double load_factor) {
		// TODO Part1: initialize table size, size, mode, and load_factor
		//             Instantiate hashTable1 and initialize it
		this.load_factor = load_factor;
		this.mode = mode;
		tableSize=INITIAL_SIZE;
		size=0;
		hashTable1=new int[INITIAL_SIZE];
		hashTable2 = new int[INITIAL_SIZE];
		hashTableLL = new LinkedList[INITIAL_SIZE];
		initHashTable(hashTable1,hashTable2);
		initHashTable(hashTableLL);
		if ((tableSize/2) < MAX_QP_OFFSET) {
			max_QP_LOOP = tableSize/2;
		}
		else {
			max_QP_LOOP = MAX_QP_OFFSET;
		}
	}

	public MyIntHash(MODE mode, double load_factor, int testSize) {
		// TODO Auto-generated constructor stub
		this.load_factor = load_factor;
		this.mode = mode;
		tableSize=testSize;
		size=0;
		hashTable1=new int[testSize];
		hashTable2 = new int[testSize];
		hashTableLL = new LinkedList[testSize];
		initHashTable(hashTable1,hashTable2);
		initHashTable(hashTableLL);
		if ((tableSize/2) < MAX_QP_OFFSET) {
			max_QP_LOOP = tableSize/2;
		}
		else {
			max_QP_LOOP = MAX_QP_OFFSET;
		}
	}

	/**
	 * Initializes the provided int[] hashTable - setting all entries to -1
	 * Note that this function will be overloaded to initialize hash tables in other modes
	 * of operation. This method should also reset size to 0!
	 *
	 * @param hashTable the hash table
	 */
	private void initHashTable(int[] hashTable) {
		// TODO Part1: Write this method 
	
		
		for(int i=0;i<tableSize;i++) {
			hashTable[i]=-1;
		}
		size=0;
	}
	private void initHashTable(int[] hashTable,int[] hashTable2) {
		// TODO Part1: Write this method 
	
		
		for(int i=0;i<tableSize;i++) {
			hashTable[i]=-1;
			hashTable2[i]=-1;
		}
		size=0;
	}
	private void initHashTable(LinkedList<Integer>[] hashtable) {
		
		for (int i=0;i<tableSize;i++) {
			hashtable[i] =null;
		}
		size=0;
	}
	/**
	 * Hash fx.  This is the hash function that translates the key into the index into the hash table.
	 *
	 * @param key the key
	 * @return the int
	 */
	private int hashFx(int key) {
		// TODO Part1: Write this method.
		
		return key%tableSize;
	}
	
	private int hashFx2(int key) {
		return (key/tableSize)%tableSize;
	}
	
	
	/**
	 * Adds the key to the hash table. Note that this is a helper function that will call the 
	 * required add function based upon the operating mode. However, before calling the specific
	 * add function, determine if the hash should be resized; if so, grow the hash.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean add(int key) {
		
		// TODO: Part2 - if adding this key would cause the the hash load to exceed the load_factor, grow the hash.
		//      Note that you cannot just use size in the numerator... 
		//      Write the code to implement this check and call growHash() if required (no parameters)
		
		if (!mode.equals(MODE.Cuckoo)&&(size+1)/(tableSize*1.0)>=load_factor) {
			
			growHash();
		}
		else if ((size+1)/(tableSize*2.0)>=load_factor){
			growHash();
		}
		switch (mode) {
			case Linear : return add_LP(key); 
			case Quadratic : return add_QP(key);
			case LinkedList: return add_LL(key);
			case Cuckoo: return add_Cuckoo(key);
			default:
				return false;
		}
	}
	
	/**
	 * Contains. Note that this is a helper function that will call the 
	 * required contains function based upon the operating mode
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean contains(int key) {
		switch (mode) {
			case Linear : return contains_LP(key);
			case Quadratic: return contains_QP(key);
			case LinkedList: return contains_LL(key);
			case Cuckoo: return contains_CC(key);
			default : return contains_LP(key);
		}
	}
	
	/**
	 * Remove. Note that this is a helper function that will call the 
	 * required remove function based upon the operating mode
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean remove(int key) {
		switch (mode) {
			case Linear : return remove_LP(key); 
			case Quadratic : return remove_QP(key);
			case LinkedList: return remove_LL(key);
			case Cuckoo: return remove_CC(key);
			default : return remove_LP(key);
		}
	}
	
	/**
	 * Grow hash. Note that this is a helper function that will call the 
	 * required overloaded growHash function based upon the operating mode.
	 * It will get the new size of the table, and then grow the Hash. Linear case
	 * is provided as an example....
	 */
	private void growHash() {
		int newSize = getNewTableSize(tableSize);
		switch (mode) {
		case Linear: growHash(hashTable1,newSize); break;
		case Quadratic :growHashQP(hashTable1,newSize); break;
		case LinkedList: growHash(hashTableLL,newSize);break;
		case Cuckoo: newSize = getNewTableSize_CC(tableSize); 
		growHash(hashTable1,hashTable2,newSize);break;
		}
	}
	
	/**
	 * Grow hash. This the specific function that will grow the hash table in Linear or 
	 * Quadratic modes. This method will:
	 * 	1. save the current hash table, 
	 *  2. create a new version of hashTable1
	 *  3. update tableSize and size
	 *  4. add all valid entries from the old hash table into the new hash table
	 * 
	 * @param table the table
	 * @param newSize the new size
	 */
	private void growHash(int[] table, int newSize) {
		// TODO Part2:  Write this method
		int[] currTable = table;
	
		 hashTable1 = new int[newSize];
		tableSize = newSize;
		clear();
	
				
		for (int i=0;i<currTable.length;i++) {
			if (currTable[i]!=-1) {
				add(currTable[i]);
			}
		}
	}
	
	/**
	 * Grow hash for qp
	 *
	 * @param table the table
	 * @param newSize the new size
	 */
	private void growHashQP(int[]table,int newSize) {
		growHash(table,newSize);
		if ((tableSize/2) < MAX_QP_OFFSET) {
			max_QP_LOOP = tableSize>>2;
		}
		else {
			max_QP_LOOP = MAX_QP_OFFSET;
		}
		
	}
	
	/**
	 * method for growing the hash for LL
	 *
	 * @param hashTable the hash table
	 * @param newSize the new size
	 */
	private void growHash(LinkedList<Integer>[] hashTable, int newSize) {
		LinkedList<Integer>[] curr = hashTable;
		hashTableLL = new LinkedList[newSize];
		tableSize = newSize;
		clear();
		for (int i=0;i<curr.length;i++) {
			if (curr[i]!=null) {
				for (int j=0;j<curr[i].size();j++) {
					add(curr[i].get(j));
				}
			}
		}
		
	}
	
	private void growHash(int[] hashTable,int[] hashTable2,int newSize) {
		int[] currTable = hashTable1;
		int[] curr2 = hashTable2;
		
		 hashTable1 = new int[newSize];
		 this.hashTable2 = new int[newSize];
		tableSize = newSize;
		clear();
	
		
		for (int i=0;i<currTable.length;i++) {
			if (currTable[i]!=-1) {
				add(currTable[i]);
			}
			if (curr2[i]!=-1) {
				add(curr2[i]);
			}
		}
	}
	
	/**
	 * Gets the new table size. Finds the next prime number
	 * that is greater than 2x the passed in size (startSize)
	 *
	 * @param startSize the start size
	 * @return the new table size
	 */
	private int getNewTableSize(int startSize) {
		// TODO Part2: Write this method
		int newSize = 2*startSize;
		while (!isPrime(newSize)) {
			newSize++;
		}
		return newSize;
	}
	
	private int getNewTableSize_CC(int startSize) {
		int newSize = startSize+1000;
		while (!isPrime(newSize)) {
			newSize++;
		}
		return newSize;
	}
	/**
	 * Checks if is prime.  
	 *
	 * @param size the size
	 * @return true, if is prime
	 */
	private boolean isPrime(int size) {
		// TODO Part2: Write this method
		for (int i=2;i<Math.sqrt(size);i++) {
			if (size%i==0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Adds the key using the Linear probing strategy:
	 * 
	 * 1) Find the first empty slot sequentially, starting at the index from hashFx(key)
	 * 2) Update the hash table with the key
	 * 3) increment the size
	 * 
	 * If no empty slots are found, return false - this would indicate that the hash needs to grow...
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	private boolean add_LP(int key) {
		// TODO Part1: Write this function
		
		int index = hashFx(key);
		
		
		for (int i=0;i<tableSize;i++) {
			
			if (hashTable1[index]==-1||hashTable1[index]==-2) {
				hashTable1[index]=key;
				size++;
				return true;
			}
			else if(hashTable1[index]==key) {
			
				return false;
			}
			index++;
			if (index >=tableSize) {
				index=0;
			}
				
			
		}
		
		return false;
	}
	
	/**
	 * Adds the key to the ap list
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	private boolean add_QP(int key) {
		
		int st_index = hashFx(key);
		for (int i=0;i<max_QP_LOOP;i++) {
			int index = (st_index+i*i)%tableSize;
			if (hashTable1[index]==-1||hashTable1[index]==-2) {
				hashTable1[index]=key;
				size++;
				return true;
			}
			if (hashTable1[index]==key) {
				return false;
			}
		}
		growHash();
		
		return add_QP(key);
	}
	
	
	/**
	 * Adds the key to the ll hash.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	private boolean add_LL(int key) {
		int index = hashFx(key);
		
		if (hashTableLL[index]==null) {
			hashTableLL[index]  = new LinkedList<Integer>();
		}
		if (hashTableLL[index].contains(key)) {
			
			return false;
		}
		else {
			size++;
			return hashTableLL[index].add(key);
		}
	}
	 private boolean add_Cuckoo(int key) {
		 if (contains_CC(key)) {
			 return false;
		 }
		 
		 if (!place_Cuckoo(key,hashTable1,key)) {
			 growHash();
			 return add_Cuckoo(key);
		 }
		 else {
			 return true;
		 }
		
	 }
	 
	 private boolean place_Cuckoo(int key,int[] hashTable, int origKey) {
		 int index;
		 int[] newTable;
		 if (hashTable==hashTable1) {
			  index = hashFx(key);
			  newTable = hashTable2;
		 }
		 else {
			  index = hashFx2(key);
			  newTable = hashTable1;
			  
		 }
		 if (hashTable[index]!=-1) {
				 int newKey = hashTable[index];
				 if (newKey==origKey&& hashTable==hashTable2) {
					 hashTable[index] = key;
					 return false;
				 }
				 hashTable[index] = key;
				 return place_Cuckoo(newKey,newTable,origKey);
			 }
			 else {
				 hashTable[index]= key;
				 size++;
				 return true;
			 }
		 
	
		 
	 }
	
	/**
	 * Contains - uses the Linear Probing method to determine if the key exists in the hash
	 * A key condition is that there are no open spaces between any values with collisions, 
	 * independent of where they are stored.
	 * 
	 * Starting a the index from hashFx(key), sequentially search through the hash until:
	 * a) the key matches the value at the index --> return true
	 * b) there is no valid data at the current index --> return false
	 * 
	 * If no matches found after walking through the entire table, return false
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	private boolean contains_LP(int key) {
		int index=hashFx(key);
		
		for (int i=0;i<tableSize;i++) {
			if (hashTable1[index]==-1) {
				return false;
			}
			else if (hashTable1[index]==key) {
				return true;
			}
			index++;
			if (index >=tableSize) {
				index=0;
			}
			
		}
		return false;
	}
	 
 	/**
 	 * contains check for qp
 	 *
 	 * @param key the key
 	 * @return true, if successful
 	 */
 	private boolean contains_QP(int key) {
			int st_index = hashFx(key);
			for (int i=0;i<max_QP_LOOP;i++) {
				int index = (st_index+i*i)%tableSize;
				if (hashTable1[index]==-1) {
					return false;
				}
				else if (hashTable1[index]==key) {
					return true;
				}
			}
			return false;
	 }
 	
 	/**
	  * Contains for LL
	  *
	  * @param key the key
	  * @return true, if successful
	  */
	 private boolean contains_LL(int key) {
 		int index = hashFx(key);
 		if (hashTableLL[index]==null) {
 			return false;
 		}
 		else {
 			return hashTableLL[index].contains(key);
 		}
 		
 	}
	 
	 private boolean contains_CC(int key) {
		 
		 return hashTable1[hashFx(key)]==key || hashTable2[hashFx2(key)]==key;
	 }
	/**
	 * Remove - uses the Linear Problem method to evict a key from the hash, if it exists
	 * A key requirement of this function is that the evicted key cannot introduce an open space
	 * if there are subsequent values which had collisions...
	 * 
	 * 1) Identify if the key exists by walking sequentially through the hash table, starting at hashFx(key) 
	 *    - if not return false,
	 * 2) from the index where the key value was found, search sequentially through the table, recording
	 *    any values that collide with hashFx(key) until either an open space if found or the full table has been processed.
	 *    If a collision was found, replace the key value with the collision value, and set the value at the collision index to an open space;
	 *    otherwise, set the key value to indicate an open space... I would recommend writing a helper method to implement this logic; it
	 *    would simply return the value to replace the key value with...
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	private boolean remove_LP(int key) {
		// TODO Part2: Write this function
		int index = hashFx(key);
		
		if (!contains_LP(key)) {
			return false;
		}
		for (int i=0;i< tableSize;i++) {
			if (hashTable1[index]==key) {
			
				break;
			}
			index++;
			if(index>=tableSize) {
				index=0;
			}
			
		}
		int target = getTarget(index);
		if (hashTable1[target]!=-1) {
			hashTable1[index]=-2;
		}
		else {
			hashTable1[index]=-1;
		}
		size--;
		return true;		
	}
	
	/**
	 * Removing for qp
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	private boolean remove_QP(int key) {
		if (!contains_QP(key)) {
			return false;
		}
		
		int index=0;
		int st_index = hashFx(key);
		for (int i=0;i<max_QP_LOOP;i++) {
			 index = (st_index+i*i)%tableSize;
			if (hashTable1[index]==key) {
				
				break;
			}
		}
		hashTable1[index] =-2;
		
		size--;
		return true;
		
	}
	
	/**
	 * Removing for LL
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	private boolean remove_LL(int key) {
		int index = hashFx(key);
		if (hashTableLL[index].remove((Integer)key)) {
			size--;
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean remove_CC(int key) {
		int index = hashFx(key);
		int index2= hashFx2(key);
		if (hashTable1[index]==key) {
			hashTable1[index] = -1;
			return true;
		}
		else if (hashTable2[index2]==key) {
			hashTable2[index2]=-1;
			size--;
			return true;
		}
		return false;
	}
	/**
	 * Gets the target, checks if the index is the last index of the table and sets the target to 0, otherwise target will be index+1
	 *
	 * @return the index
	 */
	private int getTarget(int index) {
		if (index==tableSize-1) {
			return 0;
		}
		else {
			return index+1;
		}
	}
		
	/**
	 * Gets the hash at. Returns the value of the hash at the specified index, and (if required by the operating mode) the specified offset.
	 * Use a switch statement to implement this code. This is FOR DEBUG AND TESTING PURPOSES ONLY
	 * 
	 * @param index the index
	 * @param offset the offset
	 * @return the hash at
	 */
	Integer getHashAt(int index, int offset) {
		// TODO Part1: as you code this project, you will add different cases. for now, complete the case for Linear Probing
		switch (mode) {
		case Linear : return hashTable1[index+offset];
		case Quadratic: return hashTable1[index+offset];
		case LinkedList: return contains_LL(index,offset);
		case Cuckoo: 
		if (offset==0) {
			return hashTable1[index];
		}
		else {
			return hashTable2[index];
		}
		}
		return -1;
	}
	
	/**
	 * contains method for get hashat,
	 *
	 * @param index the index
	 * @param offset the offset
	 * @return the integer
	 */
	Integer contains_LL(int index,int offset) {
		if (hashTableLL[index]==null||hashTableLL[index].size()==0) {
			return null;
		}
		if (offset <0 || offset>=hashTableLL[index].size()) {
			return -1;
		}
		return hashTableLL[index].get(offset);
	}
	
	
	
	/**
	 * Gets the number of elements in the Hash
	 *
	 * @return size
	 */
	public int size() {
		// TODO Part1: Write this method
		return size;
	}

	/**
	 * resets all entries of the hash to -1. This should reuse existing code!!
	 *
	 */
	public void clear() {
		// TODO Part1: Write this metho
		
	if (mode.equals(MODE.Quadratic) ||mode.equals(MODE.Linear)) {
		
		initHashTable(hashTable1); 
	}
	else if (mode.equals(MODE.LinkedList)){
		
		initHashTable(hashTableLL); 
	}
	else {
		
		initHashTable(hashTable1,hashTable2); 
	}
		
	
		
	}
	
	
	/**
	 * Returns a boolean to indicate of the hash is empty
	 *
	 * @return ????
	 */
	public boolean isEmpty() {
		// TODO Part1: Write this method
		return size==0;
	}

	/**
	 * Gets the load factor.
	 *
	 * @return the load factor
	 */
	public double getLoad_factor() {
		return load_factor;
	}

	/**
	 * Sets the load factor.
	 *
	 * @param load_factor the new load factor
	 */
	public void setLoad_factor(double load_factor) {
		this.load_factor = load_factor;
	}
	
	public double getCurrLoadFactor() {
		return size/(tableSize*1.0);
	}
	public double getCurrLoadFactorCC() {
		return (double)size/(double)tableSize*2.0;
	}

	/**
	 * Gets the table size.
	 *
	 * @return the table size
	 */
	public int getTableSize() {
		return tableSize;
	}

}
