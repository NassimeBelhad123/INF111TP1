package com.atoudeft.banque;

public class CompteEpargne extends CompteBancaire{
    /**
     * Crée un compte bancaire.
     *
     * @param numero numéro du compte
     * @param type   type du compte
     */

    private static final double LIMITE_SOLDE = 1000.0;
    private static final double FRAIS = 2.0;
    private double tauxInterets;


    public CompteEpargne(String numero, TypeCompte type, double tauxInterets) {
        super(numero, type);
        this.tauxInterets = tauxInterets;
    }

    @Override
    public boolean crediter(double montant) {
        if (montant > 0) {
            setSolde(getSolde() + montant);
            return true;
        }
        return false;
    }

    @Override
    public boolean debiter(double montant) {
        if (montant > 0 && getSolde() >= montant) {
            double soldeAvantOperation = getSolde();
            setSolde(getSolde()- montant); ;
            if (soldeAvantOperation < LIMITE_SOLDE) {
                setSolde(getSolde()-FRAIS);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean payerFacture(String numeroFacture, double montant, String description) {
        return false;
    }



    @Override
    public boolean transferer(double montant, String numeroCompteDestinataire) {
        return false;
    }



    public void ajouterInterets(){
        double interets = getSolde()*tauxInterets/100;
        setSolde(getSolde()+interets);
    }


    public double getTauxInterets() {
        return tauxInterets;
    }


    public void setTauxInterets(double tauxInterets) {
        this.tauxInterets = tauxInterets;
    }
}
