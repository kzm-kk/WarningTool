import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.interfaces.ECKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static ArrayList<String> memory_classname = new ArrayList<>();
    public static HashMap<String, String> memory_extend = new HashMap<>();
    public static HashMap<String, ArrayList<String>> memory_implement = new HashMap<>();
    public static HashMap<String, HashMap<String, String>> memory_classfield = new HashMap<>();
    public static HashMap<String, ArrayList<FieldDeclaration>> memory_classfield2 = new HashMap<>();
    public static HashMap<String, ArrayList<MethodDeclaration>> memory_classmethod = new HashMap<>();
    public static HashMap<String, ArrayList<MethodCallExpr>> memory_calling = new HashMap<>();
    public static HashMap<String, ArrayList<String>> memory_innerclass = new HashMap<>();

    public static void main(String[] args) throws IOException {

        SourceRoot root = new SourceRoot(Paths.get("./src/main/java"));
        System.out.println(root.toString());
        List<ParseResult<CompilationUnit>> cu2 = root.tryToParse("");

        String tmpname = "/Users/kzm0308/.gradle/caches/modules-2/files-2.1/com.github.javaparser/javaparser-core/3.16.1/440e5d7118e50d544418a96873d6322c4b1d5527";

        ProjectRoot projectRoot =
                new SymbolSolverCollectionStrategy()
                        .collect(Paths.get("/Users/kzm0308/.gradle/caches/modules-2/files-2.1/com.github.javaparser/javaparser-core/3.16.1/440e5d7118e50d544418a96873d6322c4b1d5527/javaparser-core-3.16.1.jar!/com/github/javaparser"));

        dumpFile(new File(tmpname), 0);

        //情報収集フェーズ
        for(int i = 0; i < cu2.size(); i++){
            VoidVisitor<?> visitor = new SomeVisitor();
            cu2.get(i).getResult().get().accept(visitor, null);
        }

        //判断・警告フェーズ(出力)
        check_initialize();
        judge_case1();
        judge_case2();
        //judge_case3();
        judge_case5and7("case5");
        judge_case5and7("case7");

    }

    public static void judge_case1(){
        System.out.println("all check start:case1\n");
        for(String key:memory_classname){
            HashMap<String, String> judge_hash = memory_classfield.get(key);//今のクラスのフィールド取得
            if(judge_hash != null) {
                System.out.println("check start:" + key);
                for (String extend_field : judge_hash.keySet()) {//フィールドを１つずつ見る

                    //implementsの確認、再帰関数使用
                    if (memory_implement.get(key) != null) {
                        for (String name_interface : memory_implement.get(key)) {
                            check_ImplementField(name_interface, extend_field);
                        }
                    }

                    //extendsの確認、再帰関数使用
                    if (memory_extend.get(key) != null) {
                        check_ExtendField(memory_extend.get(key), extend_field);
                    }

                }
                System.out.println("\ncheck finished:" + key + "\n");
            }
        }
        System.out.println("all check finished:case1\n");
    }

    public static void judge_case2(){
        System.out.println("all check start:case2\n");
        for(String key:memory_classmethod.keySet()) {
            System.out.println("check start:"+ key);
            for (MethodDeclaration detail : memory_classmethod.get(key)) {
                if (detail.equals(null)) break;
                //get/set探す部分
                String methodname = detail.getNameAsString();
                String cut_field = "";

                cut_field = search_get("case2", detail);
                if(!cut_field.equals("")){
                    if(!match_field(key, cut_field)) {
                        System.out.print("line " +detail.getRange().get().begin.line);
                        System.out.println("-"+detail.getRange().get().end.line);
                        System.out.println("method \"" + methodname + "\" is existing getter" +
                                " but field \"" + cut_field + "\" is not existing.");
                        System.out.println("You should change methodname from "
                                + methodname + " to " + methodname.toLowerCase() + " or other name.\n");
                    }
                }

                cut_field = search_set("case2", detail);
                if(!cut_field.equals("")){
                    if(!match_field(key, cut_field)) {
                        System.out.println(detail.getRange().get());
                        System.out.println("method \"" + methodname + "\" is existing setter" +
                                " but field \"" + cut_field + "\" is not existing.");
                        System.out.println("You should change methodname from "
                                + methodname + " to " + methodname.toLowerCase() + " or other name.\n");
                    }
                }

            }
            System.out.println("\ncheck finished:" + key + "\n");
        }
        System.out.println("all check finished:case2\n");
    }

    public static void judge_case3(){
        System.out.println("all check start:case3\n");
        for(String key:memory_innerclass.keySet()){
            System.out.println("check start:" + key);
            for(String inner:memory_innerclass.get(key)){
                System.out.println(inner);
            }
        }
        System.out.println("all check finished:case3\n");
    }

    public static void judge_case5and7(String mode){
        System.out.println("all check start:" + mode+"\n");
        for(String key:memory_classname) {
            ArrayList<MethodDeclaration> judge_array = memory_classmethod.get(key);//今のクラスのフィールド取得
            if(judge_array != null) {
                System.out.println("check start:" + key);
                for (MethodDeclaration detail:judge_array) {//フィールドを１つずつ見る
                    String cut_field = "";
                    //implementsの確認、再帰関数使用
                    if (memory_implement.get(key) != null) {
                        //System.out.println("checking:implements\n");
                        for (String name_interface : memory_implement.get(key)) {
                            cut_field = check_ImplementMethod(name_interface, mode);
                        }
                    }

                    //extendsの確認、再帰関数使用
                    if (memory_extend.get(key) != null) {
                        //System.out.println("checking:extends\n");
                        cut_field = check_ExtendMethod(memory_extend.get(key), mode);
                    }


                    boolean flag = !cut_field.equals("");
                    //get/set探す部分(継承先)
                    if(flag) {
                        ArrayList<MethodDeclaration> extend_list = memory_classmethod.get(key);
                        if(extend_list == null) continue;
                        for (MethodDeclaration md : extend_list) {
                            int size = md.getParameters().size();
                            if (size == 0) break;
                            else {
                                String methodname = md.getNameAsString();
                                for (int i = 0; i < size; i++) {
                                    String param = md.getParameter(i).getNameAsString();
                                    if (param.equals(cut_field)) {
                                        VoidVisitor<?> visitor = new Checking_Case5and7();
                                        ((Checking_Case5and7) visitor).setClassname(key);
                                        ((Checking_Case5and7) visitor).setMethodname(methodname);
                                        ((Checking_Case5and7) visitor).setLooking_argument(param);
                                        ((Checking_Case5and7) visitor).setMode(mode);
                                        md.accept(visitor, null);//visitorを利用して捜索と警告を両方行う
                                    }
                                }
                            }
                        }
                    }

                }
                System.out.println("\ncheck finished:" + key + "\n");
            }
        }
        System.out.println("all check finished:"+ mode +"\n");
    }

    public static void check_ExtendField(String origin, String extend_field){
        HashMap<String, String> check_hash = memory_classfield.get(origin);
        if(check_hash != null) {
            for (String origin_field : check_hash.keySet()) {
                if (extend_field.equals(origin_field)) {
                    System.out.println("same name field:" + origin_field + "(origin), "
                            + extend_field + "(extends)"
                            + "\nYou should change name of field(extends).\n");
                    break;
                }
            }
        }
        if(memory_extend.get(origin) != null) check_ExtendField(memory_extend.get(origin), extend_field);
        if(memory_implement.get(origin) != null){
            for(String key:memory_implement.get(origin)) check_ImplementField(key, extend_field);
        }
    }

    public static void check_ImplementField(String origin, String field){
        HashMap<String, String> check_hash = memory_classfield.get(origin);
        if(check_hash != null) {
            for (String origin_field : check_hash.keySet()) {
                if (field.equals(origin_field)) {
                    System.out.println("same name field:" + origin_field + "(interface), "
                            + field + "(implements-class)"
                            + "\nYou should change name of field(implements-class).\n");
                    break;
                }
            }
        }
        if(memory_extend.get(origin) != null) check_ExtendField(origin, field);
    }

    public static String search_get(String mode, MethodDeclaration detail){
        String methodname = detail.getNameAsString();
        String cut_field = "";

        if (methodname.matches("get[A-Z].*")) {
            if (detail.getParameters().isEmpty()) {
                String returns = detail.getTypeAsString();
                if (!returns.equals("void") || (returns.equals("void") && !mode.equals("case2"))) {
                    String str = methodname.split("get")[1].toLowerCase();
                    if(str.equals(cut_field));
                    else cut_field = methodname.split("get")[1].toLowerCase();
                }
            }
        }
        return cut_field;
    }

    public static String search_set(String mode, MethodDeclaration detail){
        String methodname = detail.getNameAsString();
        String cut_field = "";
        if (methodname.matches("set[A-Z].*")) {
            int size_param = detail.getParameters().size();
            if (size_param == 1) {
                String returns = detail.getTypeAsString();
                if (returns.equals("void")) {
                    String str = methodname.split("set")[1].toLowerCase();
                    if(str.equals(cut_field)) ;
                    else cut_field = methodname.split("set")[1].toLowerCase();
                }
            }
        }
        return cut_field;
    }

    public static boolean match_field(String method, String xxx){
        if(memory_classfield.get(method) != null) {
            for (String key : memory_classfield.get(method).keySet()) {
                if (key.equals(xxx)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String check_ExtendMethod(String origin, String mode){
        ArrayList<MethodDeclaration> check_array = memory_classmethod.get(origin);
        String cut_field = "";
        if(check_array != null) {
            for(MethodDeclaration detail:check_array) {
                String name = detail.getNameAsString();
                cut_field = search_get(mode, detail);
                if (cut_field.equals("")) {
                    cut_field = search_set(mode, detail);
                    if (cut_field.equals("")) {
                        continue;
                    } else {
                        if(match_field(origin, cut_field) && mode.equals("case7"))
                            cut_field = "";
                        else break;
                    }
                } else {
                    if(match_field(origin, cut_field) && mode.equals("case7"))
                        cut_field = "";
                    else break;
                }
            }
        }
        boolean flag = cut_field.equals("");
        if(memory_extend.get(origin) != null && flag){
            cut_field = check_ExtendMethod(memory_extend.get(origin), mode);
            flag = cut_field.equals("");
        }
        if(memory_implement.get(origin) != null && flag){
            for(String key:memory_implement.get(origin)){
                cut_field = check_ImplementMethod(key, mode);
                flag = cut_field.equals("");
                if(!flag) break;
            }
        }
        return cut_field;
    }

    public static String check_ImplementMethod(String origin, String mode){
        ArrayList<MethodDeclaration> check_array = memory_classmethod.get(origin);
        String cut_field = "";
        if(check_array != null) {
            for(MethodDeclaration detail:check_array) {
                String name = detail.getNameAsString();
                cut_field = search_get(mode, detail);
                if (cut_field.equals("")) {
                    cut_field = search_set(mode, detail);
                    if (cut_field.equals("")) {
                        continue;
                    } else {
                        if(match_field(origin, cut_field) && mode.equals("case7"))
                            cut_field = "";
                        else break;
                    }
                } else {
                    if(match_field(origin, cut_field) && mode.equals("case7"))
                        cut_field = "";
                    else break;
                }
            }
        }
        boolean flag = cut_field.equals("");
        while (flag){
            if(memory_extend.get(origin) != null){
                cut_field = check_ExtendMethod(memory_extend.get(origin), mode);
                flag = cut_field.equals("");
            } else break;
        }
        return cut_field;
    }


    private static class SomeVisitor extends VoidVisitorAdapter<Void> {
        String classname = "";
        ArrayList<MethodDeclaration> node_list = new ArrayList<>();
        HashMap<String, String> field_memory = new HashMap<>();
        ArrayList<FieldDeclaration> field_list = new ArrayList<>();
        ArrayList<String> inner_list = new ArrayList<>();

        @Override
        public void visit(ImportDeclaration md, Void arg){
            super.visit(md, arg);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration md, Void arg){
            String reserve_name = "";
            if(md.isInnerClass()) {
                if(memory_innerclass.get(classname) == null) inner_list = new ArrayList<>();
                else inner_list = memory_innerclass.get(classname);
                reserve_name = classname;
            }
            classname = md.getNameAsString();
            memory_classname.add(classname);
            System.out.println(classname);
            int size_extend = md.getExtendedTypes().size();
            int size_implement = md.getImplementedTypes().size();
            if(size_extend != 0){
                memory_extend.put(classname, md.getExtendedTypes().get(0).getNameAsString());
            }
            if(size_implement != 0){
                ArrayList<String> names = new ArrayList<>();
                for(int i = 0; i < size_implement ;i++){
                    names.add(md.getImplementedTypes(i).getNameAsString());
                }
                memory_implement.put(classname, names);
            }
            if(md.isInnerClass()){
                inner_list.add(classname);
                memory_innerclass.put(reserve_name, inner_list);
                super.visit(md, arg);
                classname = reserve_name;
            } else
            super.visit(md, arg);
        }

        @Override//宣言名
        public void visit(FieldDeclaration md, Void arg){
            field_list.add(md);
            memory_classfield2.put(classname, field_list);
            field_memory.put(md.getVariable(0).getNameAsString(),
                    md.getVariable(0).getType().toString());
            memory_classfield.put(classname, field_memory);
            super.visit(md, arg);
        }

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            node_list.add(md);
            memory_classmethod.put(classname, node_list);
            super.visit(md, arg);
        }
    }


    private static class Checking_Case5and7 extends VoidVisitorAdapter<Void> {
        ArrayList<MethodCallExpr> list = new ArrayList<>();
        String classname = "";
        String methodname = "";
        String looking_argument = "";
        String mode = "";

        public void setClassname(String name){
            classname = name;
        }

        public void setMethodname(String name){
            methodname = name;
        }

        public void setLooking_argument(String name){
            looking_argument = name;
        }

        public void setMode(String mode){
            this.mode = mode;
        }

        @Override
        public void visit(MethodCallExpr md, Void arg){
            list.add(md);
            memory_calling.put("",list);
            boolean flag = false;
            if(mode.equals("case5"))flag = md.getScope().isEmpty() ;
            else if(mode.equals("case7") && !md.getScope().isEmpty())
                flag = md.getScope().get().isThisExpr() ;
            if(flag) {
                String methodname = md.getNameAsString();
                if (methodname.matches("get[A-Z].*")) {
                    if (md.getArguments() == null) {
                        System.out.println(mode+":Future Error/"+md.getRange().get());
                    }
                } else if (methodname.matches("set[A-Z].*")) {
                    int size_param = md.getArguments().size();
                    if (size_param == 1) {
                        String argument = md.getArgument(0).toString();
                        if (argument.equals(looking_argument)) {
                            System.out.println("line "+md.getRange().get().begin.line);
                            System.out.println("This code may give the following error after converting to Kotlin: val cannnot reassigned.");
                            System.out.println("It is recommended to rename the argument \""+looking_argument+"\" in method \""+ this.methodname +"\".");
                        }
                    }
                }
            }
        }
    }

    public static void check_initialize(){
        for(String classname:memory_classname){
            ArrayList<FieldDeclaration> field_list = memory_classfield2.get(classname);
            if(field_list == null)continue;
            System.out.println("Field checking:" + classname + "\n");
            for(FieldDeclaration field:field_list){
                int size = field.getVariables().size();
                for(int i = 0;i < size; i++){
                    if(field.getVariable(i).getInitializer().isEmpty()){
                        System.out.println("line "+field.getRange().get().begin.line);
                        System.out.println("Field \""+ field.getVariable(i).getNameAsString()
                                +"\" doesn't have initializer.");
                        if(field.getVariable(i).getType().isPrimitiveType()) System.out.println("You should use modifer \"not-null\" after convert to Kotlin\n");
                        else if(field.getVariable(i).getType().isReferenceType()) System.out.println("You should use modifer \"lateinit\" after convert to Kotlin\n");
                    }
                }
            }
            System.out.println("Finished checking:" + classname + "\n");
        }
    }

    private static void dumpFile(File file, int level){

        // ファイル一覧取得
        File[] files = file.listFiles();

        if(files == null){
            return;
        }

        // インデント用の空白作成
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < level; i++){
            sb.append("  ");
        }
        String indent = sb.toString();

        System.out.println(indent + "/" + file.getName());

        for (File tmpFile : files) {

            // ディレクトリの場合
            if(tmpFile.isDirectory()){

                // 再帰呼び出し
                dumpFile(tmpFile, level + 1);

                // ファイルの場合
            }else{
                System.out.println(indent + "  " + tmpFile.getName());
            }
        }
    }

}
