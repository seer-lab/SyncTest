package account;

class Account {
    double amount;  //the money in the account
    String  name;   //account name, randomly generated in main
    int num;        //the account number, assigned in main

    //constructor
    public Account(String nm, double amnt, int num) {
        amount = amnt;
        name = nm;
        this.num = num;
    }

    //functions
    synchronized void deposit(double money) {
        // System.out.println("Depositing "+money+" to account "+num);
        amount += money;
    }

    synchronized void withdraw(double money) {
        //  System.out.println("Withdrawing "+money+" from account "+num);
        if((amount - money) < 0)
            System.out.println("ERROR Insufficient funds for withdrawl");
        amount -= money;
    }

    synchronized void transfer(Account ac, double mn) {
    //always start with the smaller account number to avoid deadlocks
    //as discussed in thesis meeting 10/08/2015
        //System.out.println("Transferring "+mn+" from account "+num+" to account "+ac.num);
        if(amount - mn < 0) {
            System.out.println("ERROR - Insufficient funds for transfer"    );
            System.exit(1);
        }

       if(ac.num < num) {
           synchronized(ac) {
               synchronized(this) {
                    amount -= mn;
                    ac.amount += mn;
               }
           }
       } else {
            synchronized(this) {
               synchronized(ac) {
                   amount -= mn;
                   ac.amount += mn;
               }
           }
       }
    }

    synchronized void print(){
        System.out.println(name + "(" + num + ") -- "+amount);
    }

}//end of class Account
