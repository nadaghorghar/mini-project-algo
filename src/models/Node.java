package models;

/**
 * Classe représentant un nœud de l'arbre AVL.
 * Chaque nœud contient une racine arabe et les références
 * vers ses enfants gauche et droit, ainsi que sa hauteur
 * pour maintenir l'équilibre de l'arbre.
 * 
 * @author Étudiant 1
 * @version 1.0
 */
public class Node {
    
    // ========== ATTRIBUTS ==========
    
    /**
     * La racine arabe stockée dans ce nœud
     */
    Root root;
    
    /**
     * Référence vers le fils gauche
     */
    public Node left;
    
    /**
     * Référence vers le fils droit
     */
    public Node right;
    
    /**
     * Hauteur du nœud dans l'arbre (pour l'équilibrage AVL)
     * Une feuille a une hauteur de 1
     */
    int height;
    
    // ========== CONSTRUCTEUR ==========
    
    /**
     * Constructeur créant un nœud avec une racine donnée.
     * Les enfants sont initialisés à null et la hauteur à 1.
     * 
     * @param root La racine à stocker dans ce nœud
     */
    public Node(Root root) {
        this.root = root;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public Root getRoot() {
        return root;
    }
    
    public void setRoot(Root root) {
        this.root = root;
    }
    
    public Node getLeft() {
        return left;
    }
    
    public void setLeft(Node left) {
        this.left = left;
    }
    
    public Node getRight() {
        return right;
    }
    
    public void setRight(Node right) {
        this.right = right;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Vérifie si ce nœud est une feuille (sans enfants).
     * 
     * @return true si le nœud est une feuille, false sinon
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }
    
    /**
     * Vérifie si ce nœud a un fils gauche.
     * 
     * @return true si le fils gauche existe
     */
    public boolean hasLeft() {
        return left != null;
    }
    
    /**
     * Vérifie si ce nœud a un fils droit.
     * 
     * @return true si le fils droit existe
     */
    public boolean hasRight() {
        return right != null;
    }
    
    /**
     * Compte le nombre d'enfants de ce nœud.
     * 
     * @return 0, 1 ou 2
     */
    public int getChildrenCount() {
        int count = 0;
        if (left != null) count++;
        if (right != null) count++;
        return count;
    }
    
    /**
     * Retourne une représentation textuelle du nœud.
     * 
     * @return Chaîne de caractères formatée
     */
    @Override
    public String toString() {
        return "Node[" + root.getLetters() + ", h=" + height + "]";
    }
}