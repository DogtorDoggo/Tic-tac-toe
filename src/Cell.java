class Cell{
    enum ROLE {
        // O for human, X for AI agent
        CIRCLE('O'), CROSS('X'), EMPTY('E');
        private char type;

        ROLE(char type){this.type = type;}
        public char getType() {return type;}

    }

    ROLE role;

    public Cell(){
        role = ROLE.EMPTY;
    }

    public Cell(char type){
        switch(type){
            case 'O': role = ROLE.CIRCLE;
                break;
            case 'X': role = ROLE.CROSS;
                break;
            default: role = ROLE.EMPTY;
        }
    }
}