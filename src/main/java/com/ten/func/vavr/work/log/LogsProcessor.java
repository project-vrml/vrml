//package com.ten.func.vavr.work.log;
//
//import com.sun.source.tree.CompilationUnitTree;
//import com.sun.source.tree.Tree;
//import com.sun.source.util.Trees;
//import com.sun.tools.javac.processing.JavacProcessingEnvironment;
//import com.sun.tools.javac.tree.JCTree;
//import com.sun.tools.javac.tree.TreeMaker;
//import com.sun.tools.javac.tree.TreeTranslator;
//import com.sun.tools.javac.util.Context;
//import com.sun.tools.javac.util.List;
//import com.sun.tools.javac.util.Names;
//
//import javax.annotation.processing.*;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ElementKind;
//import javax.lang.model.element.Name;
//import javax.lang.model.element.TypeElement;
//import javax.lang.model.util.Elements;
//import javax.lang.model.util.Types;
//import javax.tools.Diagnostic;
//import javax.tools.JavaFileObject;
//import java.text.MessageFormat;
//import java.util.Map;
//import java.util.Set;
//
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedAnnotationTypes("com.ten.func.vavr.work.log.SlfLogs")
//public class LogsProcessor extends AbstractProcessor {
//
//    private Trees trees;
//    private Names names;
//    private TreeMaker maker;
//    private Messager messager;
//
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//        messager = processingEnv.getMessager();
//        trees = Trees.instance(processingEnv);
//        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
//        maker = TreeMaker.instance(context);
//        names = Names.instance(context);
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        // 1 检查类型
//        roundEnv.getElementsAnnotatedWith(SlfLogs.class).stream().forEach(elm -> {
//            if (elm.getKind() != ElementKind.CLASS && elm.getKind() != ElementKind.ENUM) {
//                messager.printMessage(Diagnostic.Kind.ERROR, "Only classes or enums can be annotated with " + SlfLogs.class.getSimpleName());
//                return;
//            }
//
//            // 2 检查log成员变量是否已存在
//            TypeElement typeElm = (TypeElement) elm;
//            if (typeElm.getEnclosedElements().stream().anyMatch(e -> e.getKind() == ElementKind.FIELD && "Logger".contentEquals(e.getSimpleName()))) {
//                messager.printMessage(Diagnostic.Kind.WARNING, MessageFormat.format("A member field named {0} already exists in the annotated class", "Logger"));
//                return;
//            }
//
//            // 3 注入log成员变量
//            CompilationUnitTree cuTree = trees.getPath(typeElm).getCompilationUnit();
//            if (cuTree instanceof JCTree.JCCompilationUnit) {
//                JCTree.JCCompilationUnit cu = (JCTree.JCCompilationUnit) cuTree;
//                // only process on files which have been compiled from source
//                if (cu.sourcefile.getKind() == JavaFileObject.Kind.SOURCE) {
//                    _findType(cu, typeElm.getQualifiedName().toString()).ifPresent(type -> {
//                        SlfLogs slf4j = typeElm.getAnnotation(SlfLogs.class);
//                        String system = slf4j.system();
//                        String module = slf4j.module();
//
//                        // 生成private static final Logger log = LoggerFactory.getLogger(LoggerFactory.Type.SLF4J, <annotatedClass>, <system>, <module>);
//                        JCTree.JCExpression loggerType = _toExpression(Logger.class.getCanonicalName());
//                        JCTree.JCExpression getLoggerMethod = _toExpression(LoggerFactory.class.getCanonicalName() + ".getLogger");
//                        JCTree.JCExpression typeArg = _toExpression(LoggerFactory.Type.class.getCanonicalName() + "." + LoggerFactory.Type.SLF4J.name());
//                        JCTree.JCExpression nameArg = _toExpression(typeElm.getQualifiedName() + ".class");
//                        JCTree.JCExpression systemArg = maker.Literal(system);
//                        JCTree.JCExpression moduleArg = maker.Literal(module);
//                        JCTree.JCMethodInvocation getLoggerCall = maker.Apply(List.nil(), getLoggerMethod, List.of(typeArg, nameArg, systemArg, moduleArg));
//                        JCTree.JCVariableDecl logField = maker.VarDef(
//                                maker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL),
//                                names.fromString("Logger"), loggerType, getLoggerCall);
//
//                        _insertField(type, logField);
//                    });
//                }
//            }
//        });
//
//        return true;
//    }
