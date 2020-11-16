package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.awt.*;

public class Controller {

    int count = 0;

    @FXML
    public javafx.scene.control.TextArea textArea;



    public void onClickBtn(ActionEvent actionEvent) {
       textArea.appendText(((Button)actionEvent.getSource()).getText());

    }

    public void onClickBtnUndo(ActionEvent actionEvent) {

        String str = textArea.getText();
        if (str.length()>=1){
            if (str.charAt(str.length()-1) == '(' || str.charAt(str.length()-1) == ')'){
                count--;
            }
            str = str.substring(0, str.length() - 1);
            textArea.clear();
            textArea.appendText(str);
        }

    }

    public void onClickBtnClear(ActionEvent actionEvent) {
        textArea.clear();
        count=0;
    }


    public void onClickBtnResult(ActionEvent actionEvent) {
        String result = textArea.getText();
        textArea.clear();
        textArea.appendText(String.valueOf(eval(result)));

    }



    public void onClickBtnBrckt(ActionEvent actionEvent) {
        count++;
        if (count%2 == 0){
            textArea.appendText(")");
        }
        else  textArea.appendText("(");


    }

    public void onClickBtnZn(ActionEvent actionEvent) {
        Platform.exit();
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }


            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }



}
