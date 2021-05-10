package com.lox;

//Expression tree bullcrap
public class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        //calls accept() on each subexpression and passes in itself, so recursion
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }


    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }


    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    //uses spread on Expr so I can use cast multiple Exrp's
    //use Lit Expressions by converting the value to a string
    private String parenthesize(String group, Expr... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(group);
        for (Expr expr : expressions) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    //Parser
    public static void main(String[] args) {

        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));
        System.out.println(new AstPrinter().print(expression));

    }
}
