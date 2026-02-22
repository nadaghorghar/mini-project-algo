
package models;


public class Node {


    Root root;
    public Node left;
    public Node right;
    int height;


    public Node(Root root) {
        this.root = root;
        this.left = null;
        this.right = null;
        this.height = 1;
    }


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


    //noued est  feuille

    public boolean isLeaf() {
        return left == null && right == null;
    }





    @Override
    public String toString() {
        return "Node[" + root.getLetters() + ", h=" + height + "]";
    }
}
