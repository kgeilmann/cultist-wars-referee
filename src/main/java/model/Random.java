package model;

public class Random {
    static int seed = 42;
    final static int mask = 2_147_483_647;

    public static int rand(int bound) {
        seed = (seed * 1_103_515_245 + 12_345) & mask;
        return seed % bound;
    }

    public static void main(String[] args) {
        System.out.println(mask);
        for (int i = 0; i < 20; ++i) {
            System.out.println(rand(10));
        }
    }
}
