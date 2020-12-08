import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static HashMap<String, ArrayList<ImportDeclaration>> memory_import = new HashMap<>();
    public static ArrayList<String> memory_classname = new ArrayList<>();
    public static HashMap<String, String> memory_extend = new HashMap<>();
    public static HashMap<String, ArrayList<String>> memory_implement = new HashMap<>();
    public static HashMap<String, HashMap<String, String>> memory_classfield = new HashMap<>();
    public static HashMap<String, ArrayList<FieldDeclaration>> memory_classfield2 = new HashMap<>();
    public static HashMap<String, ArrayList<MethodDeclaration>> memory_classmethod = new HashMap<>();
    public static HashMap<String, ArrayList<MethodCallExpr>> memory_calling = new HashMap<>();
    public static HashMap<String, ArrayList<String>> memory_innerclass = new HashMap<>();

    public static void main(String[] args) throws IOException {

        String path_root = "/Users/kzm0308/Desktop/workspace/PartyBattleGame/app/src/main/java/com/example/kzm/partybattlegame";

        SourceRoot root = new SourceRoot(Paths.get(path_root));
        System.out.println(root.toString());
        List<ParseResult<CompilationUnit>> cu2 = root.tryToParse("");

        String tmpname = "/Users/kzm0308/.gradle/caches/modules-2/files-2.1/com.github.javaparser/javaparser-core/3.16.1/440e5d7118e50d544418a96873d6322c4b1d5527";

        ProjectRoot projectRoot =
                new SymbolSolverCollectionStrategy()
                        .collect(Paths.get("/Users/kzm0308/.gradle/caches/modules-2/files-2.1/com.github.javaparser/javaparser-core/3.16.1/440e5d7118e50d544418a96873d6322c4b1d5527/javaparser-core-3.16.1.jar!/com/github/javaparser"));

        dumpFile(new File(tmpname), 0);

        //情報収集フェーズ
        for(int i = 0; i < cu2.size(); i++){
            VoidVisitor<?> visitor = new FirstVisitor();
            cu2.get(i).getResult().get().accept(visitor, null);
        }
        //判断・警告フェーズ(出力)
        for(String classname:memory_classname){
            System.out.println("\ncheck start:"+classname+"\n");
            //check_initialize(classname);
            //judge_case1(classname);
            //judge_case2(classname);
            //judge_case3(classname);
            //judge_case5and7(classname, "case5");
            judge_case5and7(classname, "case7");
            System.out.println("check finished:"+classname+"\n");
        }

       System.out.println(memory_extend.get("MainActivity"));
        for(String name:memory_classname){
            if(name.equals(memory_extend.get("MainActivity")))System.out.println("ok");
        }

    }

    public static void judge_case1(String key){
        System.out.println("check start:case1\n");
            HashMap<String, String> judge_hash = memory_classfield.get(key);//今のクラスのフィールド取得
            if(judge_hash != null) {
                for (String extend_field : judge_hash.keySet()) {//フィールドを１つずつ見る

                    //implementsの確認、再帰関数使用
                    if (memory_implement.get(key) != null) {
                        for (String name_interface : memory_implement.get(key)) {
                            if(!check_import(name_interface))check_ImplementField(name_interface, extend_field);
                        }
                    }

                    //extendsの確認、再帰関数使用
                    if (memory_extend.get(key) != null) {
                        if(!check_import(memory_extend.get(key)))check_ExtendField(memory_extend.get(key), extend_field);
                    }

                }
            }
        System.out.println("check finished:case1\n");
    }

    public static void judge_case2(String key){
        System.out.println("check start:case2\n");
        if(memory_classmethod.get(key) != null) {
            for (MethodDeclaration detail : memory_classmethod.get(key)) {
                //get/set探す部分
                String methodname = detail.getNameAsString();
                String cut_field = "";

                if (search_get("case2", detail))
                    cut_field = methodname.split("get")[1].toLowerCase();
                if (!cut_field.equals("")) {
                    if (!match_field(key, cut_field)) {
                        System.out.print("line " + detail.getRange().get().begin.line);
                        System.out.println("-" + detail.getRange().get().end.line);
                        System.out.println("method \"" + methodname + "\" is existing getter" +
                                " but field \"" + cut_field + "\" is not existing.");
                        System.out.println("You should change methodname from "
                                + methodname + " to " + methodname.toLowerCase() + " or other name.\n");
                    }
                }
                if (search_set("case2", detail))
                    cut_field = methodname.split("set")[1].toLowerCase();
                if (!cut_field.equals("")) {
                    if (!match_field(key, cut_field)) {
                        System.out.println(detail.getRange().get());
                        System.out.println("method \"" + methodname + "\" is existing setter" +
                                " but field \"" + cut_field + "\" is not existing.");
                        System.out.println("You should change methodname from "
                                + methodname + " to " + methodname.toLowerCase() + " or other name.\n");
                    }
                }

            }
        }
        System.out.println("check finished:case2\n");
    }

    public static void judge_case3(String key){
        System.out.println("check start:case3\n");
        if(memory_innerclass.get(key) != null) {
            for (String inner : memory_innerclass.get(key)) {
                if (memory_classmethod.get(inner) == null) break;
                for (MethodDeclaration md : memory_classmethod.get(inner)) {
                    NodeList modifiers = md.getModifiers();
                    boolean flag = false;
                    int mod_size = modifiers.size();
                    if (mod_size == 0) continue;
                    String name = md.getNameAsString();
                    for (int i = 0; i < mod_size; i++) {
                        if (modifiers.get(i).toString().matches("private ")) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        for (MethodDeclaration enclosing : memory_classmethod.get(key)) {
                            VoidVisitor<?> visitor = new Checking_Case3(name);
                            enclosing.accept(visitor, null);//visitorを利用して捜索と警告を両方行う
                        }
                    }
                }
            }
        }
        System.out.println("check finished:case3\n");
    }

    public static void judge_case5and7(String key, String mode){
        System.out.println("check start:" + mode+"\n");
            ArrayList<MethodDeclaration> judge_array = memory_classmethod.get(key);//今のクラスのフィールド取得
            if(judge_array != null) {
                for (MethodDeclaration detail:judge_array) {//フィールドを１つずつ見る

                    int size = detail.getParameters().size();
                    if (size == 0) continue;
                    else {
                        String methodname = detail.getNameAsString();
                        for (int i = 0; i < size; i++) {

                            String param = detail.getParameter(i).getNameAsString();
                            VoidVisitor<?> visitor = new Checking_Case5and7_2(key, methodname, param, mode);
                            detail.accept(visitor, null);//visitorを利用して捜索と警告を両方行う

                        }
                    }

                    /*String cut_field = "";
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
                                        VoidVisitor<?> visitor = new Checking_Case5and7(key, methodname, param, mode);
                                        md.accept(visitor, null);//visitorを利用して捜索と警告を両方行う
                                    }
                                }
                            }
                        }
                    }*/

                }
            }
        System.out.println("check finished:"+ mode +"\n");
    }

    public static void check_ExtendField(String origin, String extend_field){
        check_import(origin);
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
        check_import(origin);
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

    public static boolean search_get(String mode, MethodDeclaration detail){
        String methodname = detail.getNameAsString();
        boolean flag = false;

        if (methodname.matches("get[A-Z].*")) {
            if (detail.getParameters().isEmpty()) {
                String returns = detail.getTypeAsString();
                if (!returns.equals("void") || (returns.equals("void") && !mode.equals("case2"))) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static boolean search_set(String mode, MethodDeclaration detail){
        String methodname = detail.getNameAsString();
        boolean flag = false;
        if (methodname.matches("set[A-Z].*")) {
            int size_param = detail.getParameters().size();
            if (size_param == 1) {
                String returns = detail.getTypeAsString();
                if (returns.equals("void")) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static boolean match_field(String method, String xxx){
        if(memory_classfield2.get(method) != null){
            for (FieldDeclaration field : memory_classfield2.get(method)) {
                int size = field.getVariables().size();
                for (int i = 0; i < size; i++) {
                    String fieldname = field.getVariable(i).getNameAsString();
                    if (fieldname.equals(xxx) || fieldname.toLowerCase().equals(xxx) || fieldname.toUpperCase().equals(xxx)) {
                        return true;
                    }
                }
            }
        }
        /*if(memory_classfield.get(method) != null) {
            for (String key : memory_classfield.get(method).keySet()) {
                if (key.equals(xxx)) {
                    return true;
                }
            }
        }*/
        return false;
    }

    public static String check_ExtendMethod(String origin, String mode){
        ArrayList<MethodDeclaration> check_array = memory_classmethod.get(origin);
        String cut_field = "";
        if(check_array != null) {
            for(MethodDeclaration detail:check_array) {
                String name = detail.getNameAsString();
                if(search_get(mode, detail))
                cut_field = name.split("get")[1].toLowerCase();
                if (cut_field.equals("")) {
                    if(search_set(mode, detail))
                        cut_field = name.split("set")[1].toLowerCase();
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
                if(search_get(mode, detail))
                    cut_field = name.split("get")[1].toLowerCase();
                if (cut_field.equals("")) {
                    if(search_set(mode, detail))
                        cut_field = name.split("set")[1].toLowerCase();
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

    public static boolean check_ExtendMethod2(String origin, String mode){
        ArrayList<MethodDeclaration> check_array = memory_classmethod.get(origin);
        boolean found = false;
        if(check_array != null) {
            for(MethodDeclaration detail:check_array) {
                found = search_get(mode, detail);
                if (!found) {
                    found = search_set(mode, detail);
                    if (!found) {
                        continue;
                    } else break;
                } else break;
            }
        }
        if(memory_extend.get(origin) != null && !found){
            found = check_ExtendMethod2(memory_extend.get(origin), mode);
        }
        if(memory_implement.get(origin) != null && !found){
            for(String key:memory_implement.get(origin)){
                found = check_ImplementMethod2(key, mode);
                if(found) break;
            }
        }
        found = check_import(origin);
        return found;
    }

    public static boolean check_ImplementMethod2(String origin, String mode){
        ArrayList<MethodDeclaration> check_array = memory_classmethod.get(origin);
        boolean found = false;
        if(check_array != null) {
            for(MethodDeclaration detail:check_array) {
                found = search_get(mode, detail);
                if (!found) {
                    found = search_set(mode, detail);
                    if (!found) {
                        continue;
                    } else break;
                } else break;
            }
        }
        while (found){
            if(memory_extend.get(origin) != null){
                found = check_ExtendMethod2(memory_extend.get(origin), mode);
            } else break;
        }
        found = check_import(origin);
        return found;
    }

    private static class FirstVisitor extends VoidVisitorAdapter<Void>{

        @Override
        public void visit(ImportDeclaration md, Void arg){
            super.visit(md, arg);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration md, Void arg){
            SomeVisitor visitor = new SomeVisitor(md.getNameAsString());
            md.accept(visitor, null);
        }

    }

    private static class SomeVisitor extends VoidVisitorAdapter<Void> {
        String classname = "";
        ArrayList<MethodDeclaration> node_list = new ArrayList<>();
        HashMap<String, String> field_memory = new HashMap<>();
        ArrayList<FieldDeclaration> field_list = new ArrayList<>();
        ArrayList<String> inner_list = new ArrayList<>();

        public SomeVisitor(String name){
            classname = name;
        }

        @Override
        public void visit(ImportDeclaration md, Void arg){
            super.visit(md, arg);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration md, Void arg){
            if(classname.equals(md.getNameAsString())){
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
                super.visit(md, arg);
            } else {
                if(memory_innerclass.get(classname) == null) inner_list = new ArrayList<>();
                else inner_list = memory_innerclass.get(classname);
                inner_list.add(md.getNameAsString());
                memory_innerclass.put(classname, inner_list);
                SomeVisitor visitor = new SomeVisitor(md.getNameAsString());
                md.accept(visitor, null);
            }


            /*String reserve_name = "";
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
                super.visit(md, arg);*/
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

    private static class Checking_Case3 extends VoidVisitorAdapter<Void>{
        String methodname = "";

        public Checking_Case3(String name){
            methodname = name;
        }

        @Override
        public void visit(MethodCallExpr md, Void arg){
            String calling = md.getNameAsString();
            if(calling.equals(methodname)){
                System.out.println(methodname + " is private method. " +
                        "This code can cause an error after converting to Kotlin.\n");
            }
        }

    }

    private static class Checking_Case5and7 extends VoidVisitorAdapter<Void> {
        ArrayList<MethodCallExpr> list = new ArrayList<>();
        String classname = "";
        String methodname = "";
        String looking_argument = "";
        String mode = "";
        public Checking_Case5and7(String classname,String methodname, String looking_argument, String mode){
            this.classname = classname;
            this.methodname = methodname;
            this.looking_argument = looking_argument;
            this.mode = mode;
        }

        @Override
        public void visit(MethodCallExpr md, Void arg){
            list.add(md);
            memory_calling.put("",list);
            boolean flag = false;
            if(mode.equals("case5"))flag = md.getScope().isEmpty() ;
            else if(mode.equals("case7") && !md.getScope().isEmpty())
                flag = md.getScope().get().isThisExpr();
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

    //継承先の呼び出し箇所→メソッドの存在場所の順(これまでと反対)に探す新しいバージョン
    private static class Checking_Case5and7_2 extends VoidVisitorAdapter<Void> {
        ArrayList<MethodCallExpr> list = new ArrayList<>();
        String classname = "";
        String methodname = "";
        String looking_argument = "";
        String mode = "";
        public Checking_Case5and7_2(String classname,String methodname, String looking_argument, String mode){
            this.classname = classname;
            this.methodname = methodname;
            this.looking_argument = looking_argument;
            this.mode = mode;
        }

        @Override
        public void visit(MethodCallExpr md, Void arg){

            boolean flag = false;
            if(mode.equals("case5"))flag = md.getScope().isEmpty() ;
            else if(mode.equals("case7") && !md.getScope().isEmpty())
                flag = md.getScope().get().isThisExpr();
            if(flag) {
                String methodname = md.getNameAsString();

                if (methodname.matches("get[A-Z].*")) {
                    if (md.getArguments() == null) {
                        for(MethodDeclaration mine:memory_classmethod.get(classname)){
                            String mine_name = mine.getNameAsString();
                            String mine_type = mine.getTypeAsString();
                            int mine_sizeParameter = mine.getParameters().size();
                            if(mine_name.equals(methodname)){//自分のメソッドの場合
                                break;
                            } else {//継承元クラス・インターフェースのデフォルトクラスの場合
                                boolean warning_flag = false;

                                //implementsの確認、再帰関数使用
                                if (memory_implement.get(classname) != null) {
                                    for (String name_interface : memory_implement.get(classname)) {
                                        warning_flag = check_ImplementMethod2(name_interface, mode);

                                    }
                                }

                                //extendsの確認、再帰関数使用
                                if (memory_extend.get(classname) != null && !warning_flag) {
                                    warning_flag = check_ExtendMethod2(memory_extend.get(classname), mode);
                                }

                                if(warning_flag){
                                    System.out.println("line "+md.getRange().get().begin.line);
                                    System.out.println("This code may give the following error after converting to Kotlin: val cannnot reassigned.");
                                    System.out.println("It is recommended to rename the argument \""+looking_argument+"\" in method \""+ this.methodname +"\".");
                                }
                            }
                        }
                    }
                } else if (methodname.matches("set[A-Z].*")) {
                    int size_param = md.getArguments().size();
                    if (size_param == 1) {
                        String argument = md.getArgument(0).toString();
                        String cut_field = methodname.split("set")[1].toLowerCase();
                        if (argument.equals(looking_argument) && cut_field.equals(argument)) {
                            for(MethodDeclaration mine:memory_classmethod.get(classname)) {
                                String mine_name = mine.getNameAsString();
                                String mine_type = mine.getTypeAsString();
                                int mine_sizeParameter = mine.getParameters().size();
                                if (mine_name.equals(methodname)) {//自分のメソッドの場合
                                    if(!mine_type.equals("void")){//返り値voidかつ引数１
                                        if(mine_sizeParameter == 1)break;
                                    }
                                }
                            }//継承元クラス・インターフェースのデフォルトクラスの場合
                                boolean warning_flag = false;
                                //implementsの確認、再帰関数使用
                                if (memory_implement.get(classname) != null) {
                                    for (String name_interface : memory_implement.get(classname)) {
                                        warning_flag = check_ImplementMethod2(name_interface, mode);

                                    }
                                }

                                //extendsの確認、再帰関数使用
                                if (memory_extend.get(classname) != null && !warning_flag) {
                                    warning_flag = check_ExtendMethod2(memory_extend.get(classname), mode);
                                }

                                if (warning_flag) {
                                    System.out.println("line " + md.getRange().get().begin.line);
                                    System.out.println("This code may give the following error after converting to Kotlin: val cannnot reassigned.");
                                    System.out.println("It is recommended to rename the argument \"" + looking_argument + "\" in method \"" + this.methodname + "\".");
                                }

                        }
                    }
                }
            }
        }
    }

    public static void check_initialize(String classname){
            ArrayList<FieldDeclaration> field_list = memory_classfield2.get(classname);
            if(field_list != null) {
                System.out.println("start field checking\n");
                for (FieldDeclaration field : field_list) {
                    int size = field.getVariables().size();
                    for (int i = 0; i < size; i++) {
                        if (field.getVariable(i).getInitializer().isEmpty()) {
                            System.out.println("line " + field.getRange().get().begin.line);
                            System.out.println("Field \"" + field.getVariable(i).getNameAsString()
                                    + "\" doesn't have initializer.");
                            if (field.getVariable(i).getType().isPrimitiveType())
                                System.out.println("You should use modifer \"not-null\" after convert to Kotlin\n");
                            else if (field.getVariable(i).getType().isReferenceType())
                                System.out.println("You should use modifer \"lateinit\" after convert to Kotlin\n");
                        }
                    }
                }
                System.out.println("finished field checking\n");
            }
    }

    public static boolean check_import(String checkname){
        for(String classname:memory_classname){
            if(classname.equals(checkname)){
                return false;
            }
        }
        System.out.println("\""+checkname+"\" is probably a library. Please check the contents if necessary.");
        return true;
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
