package com.atoudeft.banque;

public class OperationRetrait extends Operation{

    private double montant;

    public OperationRetrait(double montant) {
        this.montant = montant;
        this.montant = montant;
    }


    public double getMontant() {
        return montant;
    }

    @Override
    public String toString(){
        return String.format("%s %s %.2f", super.getDate(), super.getType(), montant);    }

}
