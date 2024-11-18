package com.atoudeft.banque;

public class CompteCheque extends CompteBancaire{
    /**
     * Crée un compte bancaire.
     *
     * @param numero numéro du compte
     * @param type   type du compte
     */
    public CompteCheque(String numero, TypeCompte type) {
        super(numero, type);
    }

    @Override
    public boolean crediter(double montant) {

        if(montant > 0){
            setSolde(getSolde() + montant);
            OperationDepot operation = new OperationDepot(montant);
            ajouterOperation(operation);
        }
        else{
            return false;

        }
        return true;
    }

    @Override
    public boolean debiter(double montant) {
        if(getSolde()>0 && (getSolde()-montant)>0){
            setSolde(getSolde() - montant);
        }
        OperationRetrait operation = new OperationRetrait(montant);
        ajouterOperation(operation);

        return true;
    }

    @Override
    public boolean payerFacture(String numeroFacture, double montant, String description) {
        if(montant <=0 || montant > getSolde()){
            return false;
        }
        setSolde(getSolde() - montant);

        OperationFacture operation = new OperationFacture(montant, numeroFacture, description);
        ajouterOperation(operation);
        return true;
    }

    @Override
    public boolean transferer(double montant, String numeroCompteDestinataire) {

        if(montant <=0 || montant > getSolde()){
            return false;
        }
        setSolde(getSolde() - montant);
        OperationTransfer operation = new OperationTransfer(montant, numeroCompteDestinataire);
        ajouterOperation(operation);
        return true;
    }
}
