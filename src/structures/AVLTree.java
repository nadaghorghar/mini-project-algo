package structures;

import models.Node;
import models.Root;
import java.util.ArrayList;


public class AVLTree {



    private Node root;
    private int count;


    public AVLTree() {
        this.root = null;
        this.count = 0;
    }


    public void insert(Root r) {
        if (r == null) return;

        if (search(r.getLetters()) != null) {
            return;
        }

        root = insertRec(root, r);
        count++;
    }

    public Node search(String letters) {
        return searchRec(root, letters);
    }

    public boolean contains(String letters) {
        return search(letters) != null;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return root == null;
    }


    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.getHeight();
    }

    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private void updateHeight(Node node) {
        if (node == null) return;

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);
        node.setHeight(1 + Math.max(leftHeight, rightHeight));
    }


    // Rotations pour rééquilibrer l'arbre après insertion

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }


    // Vérifie et corrige les déséquilibres selon les 4 cas AVL

    private Node balance(Node node) {
        if (node == null) return null;

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }


    // Insertion et maj des hauteurs et équilibrage

    private Node insertRec(Node node, Root r) {
        if (node == null) {
            return new Node(r);
        }

        int cmp = r.compareTo(node.getRoot());

        if (cmp < 0) {
            node.left = insertRec(node.left, r);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, r);
        } else {
            return node;
        }

        updateHeight(node);
        return balance(node);
    }

    // Recherche

    private Node searchRec(Node node, String letters) {
        if (node == null) {
            return null;
        }

        int cmp = letters.compareTo(node.getRoot().getLetters());

        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchRec(node.left, letters);
        } else {
            return searchRec(node.right, letters);
        }
    }

    // affichage


    public void displayInOrder() {
        System.out.println("=== Affichage In-Order ===");
        displayInOrderRec(root);
        System.out.println("Total: " + count + " racines");
    }

    private void displayInOrderRec(Node node) {
        if (node == null) return;

        displayInOrderRec(node.left);
        System.out.println(node.getRoot());
        displayInOrderRec(node.right);
    }

    public void displayPreOrder() {
        System.out.println("=== Affichage Pre-Order ===");
        displayPreOrderRec(root);
        System.out.println("Total: " + count + " racines");
    }

    private void displayPreOrderRec(Node node) {
        if (node == null) return;

        System.out.println(node.getRoot());
        displayPreOrderRec(node.left);
        displayPreOrderRec(node.right);
    }

    public void displayTree() {
        System.out.println("=== Structure de l'arbre AVL ===");
        if (isEmpty()) {
            System.out.println("Arbre vide");
        } else {
            displayTreeRec(root, "", true);
        }
        System.out.println("Total: " + count + " racines");
    }

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


    // Fonctionnalités supplémentaires pour explorer et analyser l'arbre

    public ArrayList<Root> getAllRoots() {
        ArrayList<Root> roots = new ArrayList<>();
        getAllRootsRec(root, roots);
        return roots;
    }

    private void getAllRootsRec(Node node, ArrayList<Root> roots) {
        if (node == null) return;

        getAllRootsRec(node.left, roots);
        roots.add(node.getRoot());
        getAllRootsRec(node.right, roots);
    }

    public void displayStatistics() {
        System.out.println("=== Statistiques de l'arbre AVL ===");
        System.out.println("Nombre de racines: " + count);
        System.out.println("Hauteur de l'arbre: " + height(root));
        System.out.println("Arbre équilibré: " + isBalanced());
    }

    public boolean isBalanced() {
        return isBalancedRec(root);
    }

    private boolean isBalancedRec(Node node) {
        if (node == null) return true;

        int balance = getBalance(node);

        if (Math.abs(balance) > 1) {
            return false;
        }

        return isBalancedRec(node.left) && isBalancedRec(node.right);
    }

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





}