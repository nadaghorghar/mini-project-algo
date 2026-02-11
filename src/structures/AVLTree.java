package structures;

import models.Node;
import models.Root;
import java.util.ArrayList;

/**
 * Classe implémentant un arbre AVL pour stocker les racines arabes.
 * Un arbre AVL est un arbre binaire de recherche auto-équilibré où
 * la différence de hauteur entre les sous-arbres gauche et droit
 * de n'importe quel nœud est au plus 1.
 * 
 * Complexité des opérations:
 * - Insertion: O(log n)
 * - Recherche: O(log n)
 * - Affichage: O(n)
 * 
 * @author Étudiant 1
 * @version 1.0
 */
public class AVLTree {
    
    // ========== ATTRIBUTS ==========
    
    /**
     * Racine de l'arbre (nœud principal)
     */
    private Node root;
    
    /**
     * Nombre de racines stockées dans l'arbre
     */
    private int count;
    
    // ========== CONSTRUCTEUR ==========
    
    /**
     * Constructeur créant un arbre AVL vide.
     */
    public AVLTree() {
        this.root = null;
        this.count = 0;
    }
    
    // ========== MÉTHODES PUBLIQUES DE GESTION ==========
    
    /**
     * Insère une nouvelle racine dans l'arbre.
     * Si la racine existe déjà, elle n'est pas insérée.
     * 
     * @param r La racine à insérer
     */
    public void insert(Root r) {
        if (r == null) return;
        
        // Vérifier si la racine existe déjà
        if (search(r.getLetters()) != null) {
            return; // Racine déjà présente, ne pas insérer
        }
        
        // Insérer et incrémenter le compteur
        root = insertRec(root, r);
        count++;
    }
    
    /**
     * Recherche une racine par ses lettres.
     * 
     * @param letters Les lettres de la racine à rechercher
     * @return Le nœud contenant la racine, ou null si non trouvée
     */
    public Node search(String letters) {
        return searchRec(root, letters);
    }
    
    /**
     * Vérifie si une racine existe dans l'arbre.
     * 
     * @param letters Les lettres de la racine à vérifier
     * @return true si la racine existe, false sinon
     */
    public boolean contains(String letters) {
        return search(letters) != null;
    }
    
    /**
     * Obtient le nombre de racines dans l'arbre.
     * 
     * @return Le nombre de racines
     */
    public int getCount() {
        return count;
    }
    
    /**
     * Vérifie si l'arbre est vide.
     * 
     * @return true si l'arbre est vide, false sinon
     */
    public boolean isEmpty() {
        return root == null;
    }
    
    // ========== MÉTHODES AVL - HAUTEUR ET ÉQUILIBRE ==========
    
    /**
     * Obtient la hauteur d'un nœud.
     * La hauteur d'un nœud null est 0.
     * 
     * @param node Le nœud dont on veut la hauteur
     * @return La hauteur du nœud
     */
    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.getHeight();
    }
    
    /**
     * Calcule le facteur d'équilibre d'un nœud.
     * Balance Factor = hauteur(sous-arbre gauche) - hauteur(sous-arbre droit)
     * 
     * Un arbre AVL respecte: -1 ≤ BF ≤ 1
     * 
     * @param node Le nœud dont on veut le facteur d'équilibre
     * @return Le facteur d'équilibre
     */
    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }
    
    /**
     * Met à jour la hauteur d'un nœud.
     * Hauteur = 1 + max(hauteur gauche, hauteur droite)
     * 
     * @param node Le nœud dont on veut mettre à jour la hauteur
     */
    private void updateHeight(Node node) {
        if (node == null) return;
        
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);
        node.setHeight(1 + Math.max(leftHeight, rightHeight));
    }
    
    // ========== ROTATIONS AVL ==========
    
    /**
     * Effectue une rotation droite sur le nœud y.
     * 
     *       y                    x
     *      / \                  / \
     *     x   T3   ------>     T1  y
     *    / \                      / \
     *   T1  T2                   T2  T3
     * 
     * @param y Le nœud à faire pivoter
     * @return La nouvelle racine du sous-arbre (x)
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        
        // Effectuer la rotation
        x.right = y;
        y.left = T2;
        
        // Mettre à jour les hauteurs
        updateHeight(y);
        updateHeight(x);
        
        // Retourner la nouvelle racine
        return x;
    }
    
    /**
     * Effectue une rotation gauche sur le nœud x.
     * 
     *     x                        y
     *    / \                      / \
     *   T1  y      ------>       x   T3
     *      / \                  / \
     *     T2  T3               T1  T2
     * 
     * @param x Le nœud à faire pivoter
     * @return La nouvelle racine du sous-arbre (y)
     */
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        
        // Effectuer la rotation
        y.left = x;
        x.right = T2;
        
        // Mettre à jour les hauteurs
        updateHeight(x);
        updateHeight(y);
        
        // Retourner la nouvelle racine
        return y;
    }
    
    // ========== ÉQUILIBRAGE ==========
    
    /**
     * Équilibre un nœud après insertion ou suppression.
     * Gère les 4 cas de déséquilibre AVL:
     * 
     * 1. Gauche-Gauche (LL): rotation droite
     * 2. Gauche-Droite (LR): rotation gauche puis droite
     * 3. Droite-Droite (RR): rotation gauche
     * 4. Droite-Gauche (RL): rotation droite puis gauche
     * 
     * @param node Le nœud à équilibrer
     * @return Le nœud équilibré
     */
    private Node balance(Node node) {
        if (node == null) return null;
        
        int balance = getBalance(node);
        
        // Cas Gauche-Gauche (LL)
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }
        
        // Cas Gauche-Droite (LR)
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        
        // Cas Droite-Droite (RR)
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }
        
        // Cas Droite-Gauche (RL)
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        return node;
    }
    
    // ========== INSERTION RÉCURSIVE ==========
    
    /**
     * Insère récursivement une racine dans le sous-arbre enraciné en node.
     * Effectue l'équilibrage AVL après l'insertion.
     * 
     * @param node La racine du sous-arbre actuel
     * @param r La racine à insérer
     * @return La nouvelle racine du sous-arbre après insertion et équilibrage
     */
    private Node insertRec(Node node, Root r) {
        // ÉTAPE 1: INSERTION BST STANDARD
        if (node == null) {
            return new Node(r);
        }
        
        int cmp = r.compareTo(node.getRoot());
        
        if (cmp < 0) {
            node.left = insertRec(node.left, r);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, r);
        } else {
            // La racine existe déjà, ne pas insérer
            return node;
        }
        
        // ÉTAPE 2: METTRE À JOUR LA HAUTEUR
        updateHeight(node);
        
        // ÉTAPE 3: ÉQUILIBRER
        return balance(node);
    }
    
    // ========== RECHERCHE RÉCURSIVE ==========
    
    /**
     * Recherche récursivement une racine dans le sous-arbre enraciné en node.
     * 
     * @param node La racine du sous-arbre actuel
     * @param letters Les lettres de la racine recherchée
     * @return Le nœud contenant la racine, ou null
     */
    private Node searchRec(Node node, String letters) {
        // Cas de base: arbre vide
        if (node == null) {
            return null;
        }
        
        // Comparer les lettres
        int cmp = letters.compareTo(node.getRoot().getLetters());
        
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchRec(node.left, letters);
        } else {
            return searchRec(node.right, letters);
        }
    }
    
    // ========== MÉTHODES D'AFFICHAGE ==========
    
    /**
     * Affiche toutes les racines en ordre (In-Order Traversal).
     * Ordre: Gauche → Racine → Droite
     */
    public void displayInOrder() {
        System.out.println("=== Affichage In-Order ===");
        displayInOrderRec(root);
        System.out.println("Total: " + count + " racines");
    }
    
    /**
     * Parcours récursif en ordre.
     * 
     * @param node Le nœud actuel
     */
    private void displayInOrderRec(Node node) {
        if (node == null) return;
        
        displayInOrderRec(node.left);
        System.out.println(node.getRoot());
        displayInOrderRec(node.right);
    }
    
    /**
     * Affiche toutes les racines en pré-ordre (Pre-Order Traversal).
     * Ordre: Racine → Gauche → Droite
     */
    public void displayPreOrder() {
        System.out.println("=== Affichage Pre-Order ===");
        displayPreOrderRec(root);
        System.out.println("Total: " + count + " racines");
    }
    
    /**
     * Parcours récursif en pré-ordre.
     * 
     * @param node Le nœud actuel
     */
    private void displayPreOrderRec(Node node) {
        if (node == null) return;
        
        System.out.println(node.getRoot());
        displayPreOrderRec(node.left);
        displayPreOrderRec(node.right);
    }
    
    /**
     * Affiche l'arbre de manière structurée (arborescence visuelle).
     */
    public void displayTree() {
        System.out.println("=== Structure de l'arbre AVL ===");
        if (isEmpty()) {
            System.out.println("Arbre vide");
        } else {
            displayTreeRec(root, "", true);
        }
        System.out.println("Total: " + count + " racines");
    }
    
    /**
     * Affichage récursif de la structure de l'arbre.
     * 
     * @param node Le nœud actuel
     * @param prefix Le préfixe pour l'indentation
     * @param isLeft true si c'est un fils gauche
     */
    private void displayTreeRec(Node node, String prefix, boolean isLeft) {
        if (node == null) return;
        
        System.out.println(prefix + (isLeft ? "├── " : "└── ") + node.getRoot().getLetters());
        
        String newPrefix = prefix + (isLeft ? "│   " : "    ");
        
        if (node.left != null) {
            displayTreeRec(node.left, newPrefix, true);
        }
        if (node.right != null) {
            displayTreeRec(node.right, newPrefix, false);
        }
    }
    
    // ========== MÉTHODES UTILITAIRES AVANCÉES ==========
    
    /**
     * Récupère toutes les racines dans une liste.
     * 
     * @return Liste de toutes les racines
     */
    public ArrayList<Root> getAllRoots() {
        ArrayList<Root> roots = new ArrayList<>();
        getAllRootsRec(root, roots);
        return roots;
    }
    
    /**
     * Parcours récursif pour récupérer toutes les racines.
     * 
     * @param node Le nœud actuel
     * @param roots La liste où ajouter les racines
     */
    private void getAllRootsRec(Node node, ArrayList<Root> roots) {
        if (node == null) return;
        
        getAllRootsRec(node.left, roots);
        roots.add(node.getRoot());
        getAllRootsRec(node.right, roots);
    }
    
    /**
     * Affiche les statistiques de l'arbre.
     */
    public void displayStatistics() {
        System.out.println("=== Statistiques de l'arbre AVL ===");
        System.out.println("Nombre de racines: " + count);
        System.out.println("Hauteur de l'arbre: " + height(root));
        System.out.println("Arbre équilibré: " + isBalanced());
    }
    
    /**
     * Vérifie si l'arbre est équilibré.
     * 
     * @return true si équilibré, false sinon
     */
    public boolean isBalanced() {
        return isBalancedRec(root);
    }
    
    /**
     * Vérification récursive de l'équilibre.
     * 
     * @param node Le nœud actuel
     * @return true si le sous-arbre est équilibré
     */
    private boolean isBalancedRec(Node node) {
        if (node == null) return true;
        
        int balance = getBalance(node);
        
        if (Math.abs(balance) > 1) {
            return false;
        }
        
        return isBalancedRec(node.left) && isBalancedRec(node.right);
    }
    
    /**
     * Calcule la hauteur maximale de l'arbre.
     * 
     * @return La hauteur
     */
    public int getMaxHeight() {
        return height(root);
    }
    public int size() {
        return countNodes(root);
    }

    private int countNodes(Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    public String getInOrderString() {
        StringBuilder sb = new StringBuilder();
        getInOrderStringHelper(root, sb);
        sb.append("\nTotal: ").append(size()).append(" racines");
        return sb.toString();
    }

    private void getInOrderStringHelper(Node node, StringBuilder sb) {
        if (node != null) {
            getInOrderStringHelper(node.left, sb);
            sb.append(node.getRoot().toString()).append("\n");
            getInOrderStringHelper(node.right, sb);
        }
    }
}