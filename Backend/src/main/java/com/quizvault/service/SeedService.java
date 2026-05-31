package com.quizvault.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizvault.entity.SubjectQuestion;
import com.quizvault.entity.User;
import com.quizvault.repository.SubjectQuestionRepository;
import com.quizvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SeedService implements CommandLineRunner {

    @Autowired
    private SubjectQuestionRepository subjectQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        seedInitialData();
    }

    public void seedInitialData() {
        // Seed default admin user if not exists
        if (!userRepository.existsByEmail("admin@quizvault.com")) {
            User admin = User.builder()
                    .email("admin@quizvault.com")
                    .password(passwordEncoder.encode("admin123"))
                    .displayName("System Admin")
                    .role("admin")
                    .build();
            userRepository.save(admin);
        }

        if (subjectQuestionRepository.count() >= 120) {
            return;
        }

        // Clean re-seed to ensure all banks have exactly 10 questions each
        subjectQuestionRepository.deleteAll();

        try {
            // =========================================================================
            // C PROGRAMMING QUESTIONS (Banks 1 - 4, 10 Questions each = 40 Questions)
            // =========================================================================
            // Bank 1
            addQuestion("C", 1, "Which keyword is used to prevent modification of a variable in C?",
                    Arrays.asList("static", "const", "volatile", "immutable"), "const");
            addQuestion("C", 1, "What is the size of int data type in C on standard 32/64-bit systems?",
                    Arrays.asList("2 bytes", "4 bytes", "8 bytes", "1 byte"), "4 bytes");
            addQuestion("C", 1, "Which header file is required for printf() and scanf()?",
                    Arrays.asList("<stdlib.h>", "<stdio.h>", "<string.h>", "<conio.h>"), "<stdio.h>");
            addQuestion("C", 1, "What operator is used to access the address of a variable?",
                    Arrays.asList("*", "&", "->", "."), "&");
            addQuestion("C", 1, "Which loop executes at least once even if the condition is false?",
                    Arrays.asList("for", "while", "do-while", "foreach"), "do-while");
            addQuestion("C", 1, "What is the correct way to terminate a statement in C?",
                    Arrays.asList(".", ":", ";", "!"), ";");
            addQuestion("C", 1, "Which data type is used to store fractional numbers in C?",
                    Arrays.asList("int", "char", "float", "boolean"), "float");
            addQuestion("C", 1, "What is the escape character used for newline in C?",
                    Arrays.asList("\\t", "\\n", "\\0", "\\r"), "\\n");
            addQuestion("C", 1, "Which operator is used for checking equality in C?",
                    Arrays.asList("=", "==", "!=", "<>"), "==");
            addQuestion("C", 1, "What is the return type of main() function in modern C?",
                    Arrays.asList("void", "int", "float", "char"), "int");

            // Bank 2
            addQuestion("C", 2, "What value does malloc() return if memory allocation fails?",
                    Arrays.asList("0", "NULL", "-1", "void"), "NULL");
            addQuestion("C", 2, "Which operator is used to access structure members through a pointer?",
                    Arrays.asList(".", "->", "*", "&"), "->");
            addQuestion("C", 2, "What is the result of sizeof('a') in C?",
                    Arrays.asList("1", "2", "4", "8"), "4");
            addQuestion("C", 2, "Which format specifier is used to print a string in C?",
                    Arrays.asList("%c", "%s", "%d", "%str"), "%s");
            addQuestion("C", 2, "What does the 'static' keyword do to a local variable in C?",
                    Arrays.asList("Makes it constant", "Retains value between function calls", "Makes it global", "Allocates heap memory"), "Retains value between function calls");
            addQuestion("C", 2, "Which standard library function calculates the length of a string?",
                    Arrays.asList("strlength()", "strlen()", "size()", "count()"), "strlen()");
            addQuestion("C", 2, "How is a character constant enclosed in C?",
                    Arrays.asList("Single quotes ' '", "Double quotes \" \"", "Backticks ` `", "Parentheses ( )"), "Single quotes ' '");
            addQuestion("C", 2, "What is the result of 7 % 3 in C?",
                    Arrays.asList("2", "1", "0", "3"), "1");
            addQuestion("C", 2, "Which function reads a single character from standard input in C?",
                    Arrays.asList("putchar()", "getchar()", "getc()", "read()"), "getchar()");
            addQuestion("C", 2, "What is an array in C?",
                    Arrays.asList("Collection of different data types", "Collection of elements of identical data type", "A dynamic list", "A pointer object"), "Collection of elements of identical data type");

            // Bank 3
            addQuestion("C", 3, "What does free() function do in C?",
                    Arrays.asList("Deletes a file", "Frees allocated memory", "Clears the console", "Resets variables"), "Frees allocated memory");
            addQuestion("C", 3, "Which function is used to compare two strings in C?",
                    Arrays.asList("strcmp()", "strcat()", "strcpy()", "strlen()"), "strcmp()");
            addQuestion("C", 3, "What is a dangling pointer in C?",
                    Arrays.asList("Pointer pointing to NULL", "Pointer pointing to deallocated memory", "Uninitialized pointer", "Function pointer"), "Pointer pointing to deallocated memory");
            addQuestion("C", 3, "Which header file contains calloc() and malloc()?",
                    Arrays.asList("<stdio.h>", "<stdlib.h>", "<math.h>", "<memory.h>"), "<stdlib.h>");
            addQuestion("C", 3, "What does the 'break' statement do in a loop?",
                    Arrays.asList("Skips current iteration", "Terminates the loop", "Restarts the loop", "Exits the program"), "Terminates the loop");
            addQuestion("C", 3, "Which function copies one string to another in C?",
                    Arrays.asList("strcopy()", "strcpy()", "strcat()", "strdup()"), "strcpy()");
            addQuestion("C", 3, "What is the index of the first element in a C array?",
                    Arrays.asList("0", "1", "-1", "Depends on declaration"), "0");
            addQuestion("C", 3, "What is a NULL pointer in C?",
                    Arrays.asList("Pointer pointing to address 0", "Uninitialized pointer", "Void pointer", "Dangling pointer"), "Pointer pointing to address 0");
            addQuestion("C", 3, "Which operator is used for logical AND in C?",
                    Arrays.asList("&", "&&", "|", "AND"), "&&");
            addQuestion("C", 3, "What is the purpose of the 'continue' statement in C?",
                    Arrays.asList("Exits loop immediately", "Skips remainder of current iteration", "Stops program execution", "Restarts function"), "Skips remainder of current iteration");

            // Bank 4
            addQuestion("C", 4, "What type of storage class stores variables in CPU registers?",
                    Arrays.asList("auto", "extern", "register", "static"), "register");
            addQuestion("C", 4, "What happens if an array index goes out of bounds in C?",
                    Arrays.asList("Compilation error", "Undefined behavior", "Array overflow exception", "Returns zero"), "Undefined behavior");
            addQuestion("C", 4, "What is the operator used for bitwise AND in C?",
                    Arrays.asList("&&", "&", "|", "^"), "&");
            addQuestion("C", 4, "What does the preprocessor directive #include do?",
                    Arrays.asList("Compiles code", "Inserts contents of header file", "Allocates memory", "Executes system commands"), "Inserts contents of header file");
            addQuestion("C", 4, "Which function is used to concatenate two strings in C?",
                    Arrays.asList("strjoin()", "strcat()", "concat()", "append()"), "strcat()");
            addQuestion("C", 4, "What is the difference between union and structure in C?",
                    Arrays.asList("Unions have no members", "Union members share the same memory location", "Structures cannot hold pointers", "Unions cannot store integers"), "Union members share the same memory location");
            addQuestion("C", 4, "Which bitwise operator is used for XOR operation in C?",
                    Arrays.asList("&", "|", "^", "~"), "^");
            addQuestion("C", 4, "What is the format specifier for a pointer address in printf()?",
                    Arrays.asList("%d", "%p", "%x", "%a"), "%p");
            addQuestion("C", 4, "What keyword defines an enumerated data type in C?",
                    Arrays.asList("enum", "struct", "typedef", "define"), "enum");
            addQuestion("C", 4, "Which function dynamically allocates memory initialized to zero?",
                    Arrays.asList("malloc()", "calloc()", "realloc()", "alloc()"), "calloc()");

            // =========================================================================
            // PYTHON QUESTIONS (Banks 1 - 4, 10 Questions each = 40 Questions)
            // =========================================================================
            // Bank 1
            addQuestion("Python", 1, "Which data structure in Python is immutable?",
                    Arrays.asList("List", "Dictionary", "Tuple", "Set"), "Tuple");
            addQuestion("Python", 1, "How do you define a function in Python?",
                    Arrays.asList("func myFunc():", "def myFunc():", "function myFunc():", "define myFunc():"), "def myFunc():");
            addQuestion("Python", 1, "What is the output of print(type([]))?",
                    Arrays.asList("<class 'tuple'>", "<class 'list'>", "<class 'array'>", "<class 'set'>"), "<class 'list'>");
            addQuestion("Python", 1, "Which keyword is used for exception handling in Python?",
                    Arrays.asList("try / catch", "try / except", "do / handle", "try / finally only"), "try / except");
            addQuestion("Python", 1, "What symbol is used to start single-line comments in Python?",
                    Arrays.asList("//", "/*", "#", "--"), "#");
            addQuestion("Python", 1, "Which function is used to get user input in Python 3?",
                    Arrays.asList("scan()", "input()", "read()", "cin()"), "input()");
            addQuestion("Python", 1, "What is the result of 10 // 3 in Python?",
                    Arrays.asList("3.33", "3", "4", "1"), "3");
            addQuestion("Python", 1, "Which built-in function returns the length of a sequence?",
                    Arrays.asList("size()", "length()", "len()", "count()"), "len()");
            addQuestion("Python", 1, "How do you start a block of code in Python?",
                    Arrays.asList("Curly braces {}", "Indentation", "Begin / End", "Parentheses ()"), "Indentation");
            addQuestion("Python", 1, "What is the boolean value of an empty list [] in Python?",
                    Arrays.asList("True", "False", "None", "0"), "False");

            // Bank 2
            addQuestion("Python", 2, "Which method adds an item to the end of a list in Python?",
                    Arrays.asList("add()", "insert()", "append()", "push()"), "append()");
            addQuestion("Python", 2, "What is the result of 3 ** 2 in Python?",
                    Arrays.asList("6", "9", "8", "5"), "9");
            addQuestion("Python", 2, "Which built-in module is used for generating random numbers in Python?",
                    Arrays.asList("math", "random", "sys", "os"), "random");
            addQuestion("Python", 2, "What does range(5) produce when iterated over?",
                    Arrays.asList("1, 2, 3, 4, 5", "0, 1, 2, 3, 4", "0, 1, 2, 3, 4, 5", "1 to 4"), "0, 1, 2, 3, 4");
            addQuestion("Python", 2, "How do you insert an element at a specific index in a Python list?",
                    Arrays.asList("list.add(idx, val)", "list.insert(idx, val)", "list.append(idx, val)", "list.put(idx, val)"), "list.insert(idx, val)");
            addQuestion("Python", 2, "Which method removes and returns the last element from a list?",
                    Arrays.asList("pop()", "remove()", "delete()", "shift()"), "pop()");
            addQuestion("Python", 2, "What is a dictionary in Python?",
                    Arrays.asList("Ordered list of numbers", "Key-value pair mapping", "Immutable array", "Set of unique characters"), "Key-value pair mapping");
            addQuestion("Python", 2, "Which method returns all the keys of a dictionary?",
                    Arrays.asList("dict.allKeys()", "dict.keys()", "dict.get_keys()", "dict.values()"), "dict.keys()");
            addQuestion("Python", 2, "How do you convert a string to uppercase in Python?",
                    Arrays.asList("str.toUpperCase()", "str.upper()", "str.toUpper()", "upper(str)"), "str.upper()");
            addQuestion("Python", 2, "What is the keyword used to create an anonymous function in Python?",
                    Arrays.asList("def", "func", "lambda", "inline"), "lambda");

            // Bank 3
            addQuestion("Python", 3, "What is list comprehension syntax in Python used for?",
                    Arrays.asList("Creating formatted output", "Creating lists concisely", "Merging dictionaries", "Handling exceptions"), "Creating lists concisely");
            addQuestion("Python", 3, "Which function converts a string to an integer in Python?",
                    Arrays.asList("str()", "int()", "parse()", "toInteger()"), "int()");
            addQuestion("Python", 3, "What is the output of bool('False') in Python?",
                    Arrays.asList("False", "True", "None", "TypeError"), "True");
            addQuestion("Python", 3, "Which keyword is used to import code from another module?",
                    Arrays.asList("include", "import", "using", "require"), "import");
            addQuestion("Python", 3, "What does the `pass` statement do in Python?",
                    Arrays.asList("Exits the program", "A null statement (does nothing)", "Passes arguments to function", "Skips loop iteration"), "A null statement (does nothing)");
            addQuestion("Python", 3, "Which collection type in Python does NOT allow duplicate elements?",
                    Arrays.asList("List", "Tuple", "Set", "Dictionary"), "Set");
            addQuestion("Python", 3, "How do you open a file for reading in Python?",
                    Arrays.asList("open('file.txt', 'w')", "open('file.txt', 'r')", "file.read('file.txt')", "read('file.txt')"), "open('file.txt', 'r')");
            addQuestion("Python", 3, "What does `is` operator test for in Python?",
                    Arrays.asList("Value equality", "Object identity (memory location)", "Data type", "Subclass relationship"), "Object identity (memory location)");
            addQuestion("Python", 3, "Which string method removes whitespace from both ends?",
                    Arrays.asList("clean()", "strip()", "trim()", "cut()"), "strip()");
            addQuestion("Python", 3, "What is the result of 'abc' * 2 in Python?",
                    Arrays.asList("abc2", "abcabc", "TypeError", "aabbcc"), "abcabc");

            // Bank 4
            addQuestion("Python", 4, "What is PEP 8 in Python?",
                    Arrays.asList("A compiler", "Python style guide", "A package manager", "A security protocol"), "Python style guide");
            addQuestion("Python", 4, "How do you remove keys from a Python dictionary?",
                    Arrays.asList("dict.remove(key)", "del dict[key]", "dict.erase(key)", "dict.clean(key)"), "del dict[key]");
            addQuestion("Python", 4, "What is `__init__` in a Python class?",
                    Arrays.asList("Destructor", "Constructor", "Class method", "Module initializer"), "Constructor");
            addQuestion("Python", 4, "What is the default return value of a Python function without a return statement?",
                    Arrays.asList("0", "False", "None", "empty string"), "None");
            addQuestion("Python", 4, "Which keyword is used to create a generator function in Python?",
                    Arrays.asList("generate", "yield", "return", "produce"), "yield");
            addQuestion("Python", 4, "What built-in function returns an iterator of tuples pairing elements from multiple iterables?",
                    Arrays.asList("map()", "zip()", "filter()", "pair()"), "zip()");
            addQuestion("Python", 4, "How do you handle multiple exceptions in a single except block?",
                    Arrays.asList("except (ValueError, TypeError):", "except ValueError or TypeError:", "except ValueError, TypeError:", "except [ValueError, TypeError]:"), "except (ValueError, TypeError):");
            addQuestion("Python", 4, "What is the output of 'hello'[1:4] in Python?",
                    Arrays.asList("ell", "ello", "hel", "he"), "ell");
            addQuestion("Python", 4, "Which decorator is used to define a static method in a Python class?",
                    Arrays.asList("@static", "@staticmethod", "@classstatic", "@method"), "@staticmethod");
            addQuestion("Python", 4, "What module in Python standard library is used for regular expressions?",
                    Arrays.asList("regex", "re", "regexp", "string"), "re");

            // =========================================================================
            // JAVA QUESTIONS (Banks 1 - 4, 10 Questions each = 40 Questions)
            // =========================================================================
            // Bank 1
            addQuestion("Java", 1, "Which of these is NOT a primitive data type in Java?",
                    Arrays.asList("int", "boolean", "String", "double"), "String");
            addQuestion("Java", 1, "Which keyword is used to inherit a class in Java?",
                    Arrays.asList("implements", "extends", "inherits", "using"), "extends");
            addQuestion("Java", 1, "What is the entry point method signature of a Java program?",
                    Arrays.asList("public static void main(String[] args)", "public void main(String args)", "static main(String[] args)", "void main()"), "public static void main(String[] args)");
            addQuestion("Java", 1, "Which access modifier allows visibility only within the same package and subclasses?",
                    Arrays.asList("public", "private", "protected", "default"), "protected");
            addQuestion("Java", 1, "What is JVM in Java?",
                    Arrays.asList("Java Virtual Machine", "Java Variable Manager", "Java Version Module", "Java Verification Model"), "Java Virtual Machine");
            addQuestion("Java", 1, "Which keyword is used to create an instance of a class in Java?",
                    Arrays.asList("create", "alloc", "new", "instantiate"), "new");
            addQuestion("Java", 1, "What is the size of boolean data type in Java?",
                    Arrays.asList("1 bit / JVM dependent", "1 byte", "2 bytes", "4 bytes"), "1 bit / JVM dependent");
            addQuestion("Java", 1, "Which operator is used for string concatenation in Java?",
                    Arrays.asList(".", "+", "&", "concat"), "+");
            addQuestion("Java", 1, "What is the default value of an integer array element in Java?",
                    Arrays.asList("0", "null", "-1", "undefined"), "0");
            addQuestion("Java", 1, "Which class is the superclass of all classes in Java?",
                    Arrays.asList("Object", "Class", "System", "Base"), "Object");

            // Bank 2
            addQuestion("Java", 2, "Which interface is the root of the Java Collection Framework hierarchy?",
                    Arrays.asList("Collection", "Iterable", "List", "Set"), "Collection");
            addQuestion("Java", 2, "What happens when a class implements an interface in Java?",
                    Arrays.asList("It inherits instance variables", "It must provide implementations for abstract methods", "It becomes abstract", "It overrides private methods"), "It must provide implementations for abstract methods");
            addQuestion("Java", 2, "What is the difference between String and StringBuilder in Java?",
                    Arrays.asList("String is mutable, StringBuilder is immutable", "String is immutable, StringBuilder is mutable", "Both are immutable", "Both are mutable"), "String is immutable, StringBuilder is mutable");
            addQuestion("Java", 2, "Which keyword prevents method overriding in Java?",
                    Arrays.asList("static", "final", "abstract", "const"), "final");
            addQuestion("Java", 2, "Which exception is thrown when dividing an integer by zero in Java?",
                    Arrays.asList("NullPointerException", "ArithmeticException", "NumberFormatException", "IllegalArgumentException"), "ArithmeticException");
            addQuestion("Java", 2, "Which collection class allows duplicate elements and maintains insertion order?",
                    Arrays.asList("HashSet", "TreeSet", "ArrayList", "HashMap"), "ArrayList");
            addQuestion("Java", 2, "What is method overloading in Java?",
                    Arrays.asList("Same method name with different parameters", "Same method name with same parameters in subclass", "Writing long methods", "Overriding parent methods"), "Same method name with different parameters");
            addQuestion("Java", 2, "Which keyword is used to refer to current class instance in Java?",
                    Arrays.asList("this", "self", "super", "current"), "this");
            addQuestion("Java", 2, "What does JVM do with bytecodes in a .class file?",
                    Arrays.asList("Compiles them to C code", "Executes/interprets them on host machine", "Deletes them after run", "Uploads to server"), "Executes/interprets them on host machine");
            addQuestion("Java", 2, "Which keyword is used to define constant variables in Java?",
                    Arrays.asList("const", "final", "static", "immutable"), "final");

            // Bank 3
            addQuestion("Java", 3, "What is the memory area where Java objects are created?",
                    Arrays.asList("Stack", "Heap", "Metaspace", "Register"), "Heap");
            addQuestion("Java", 3, "Which keyword is used to explicitly throw an exception in Java?",
                    Arrays.asList("throw", "throws", "catch", "raise"), "throw");
            addQuestion("Java", 3, "What does garbage collection in Java do?",
                    Arrays.asList("Deletes uncompiled code", "Frees memory occupied by unreachable objects", "Clears cache memory", "Deletes temporary files"), "Frees memory occupied by unreachable objects");
            addQuestion("Java", 3, "Which collection class stores elements as unique keys mapped to values?",
                    Arrays.asList("ArrayList", "HashMap", "HashSet", "LinkedList"), "HashMap");
            addQuestion("Java", 3, "What is the default value of a boolean instance variable in Java?",
                    Arrays.asList("true", "false", "null", "0"), "false");
            addQuestion("Java", 3, "Which keyword is used to call a superclass constructor in Java?",
                    Arrays.asList("this()", "super()", "parent()", "base()"), "super()");
            addQuestion("Java", 3, "Which package is automatically imported into every Java program?",
                    Arrays.asList("java.util", "java.lang", "java.io", "java.net"), "java.lang");
            addQuestion("Java", 3, "What is an abstract class in Java?",
                    Arrays.asList("A class that cannot be instantiated directly", "A class with only static methods", "A class without constructors", "A final class"), "A class that cannot be instantiated directly");
            addQuestion("Java", 3, "Which keyword is used to declare a block synchronized for multithreading?",
                    Arrays.asList("volatile", "synchronized", "threadsafe", "atomic"), "synchronized");
            addQuestion("Java", 3, "What is the return type of equals() method in Object class?",
                    Arrays.asList("int", "boolean", "void", "String"), "boolean");

            // Bank 4
            addQuestion("Java", 4, "Which annotation indicates that a method overrides a superclass method?",
                    Arrays.asList("@Overload", "@Override", "@Super", "@Implement"), "@Override");
            addQuestion("Java", 4, "Which method is called before an object is garbage collected (deprecated in modern Java)?",
                    Arrays.asList("destroy()", "finalize()", "clean()", "dispose()"), "finalize()");
            addQuestion("Java", 4, "What is the difference between throw and throws in Java?",
                    Arrays.asList("throw declares exceptions; throws triggers exception", "throw triggers an exception; throws declares exceptions in method signature", "Both are identical", "throws is used in catch block"), "throw triggers an exception; throws declares exceptions in method signature");
            addQuestion("Java", 4, "Which interface must be implemented to create a thread in Java?",
                    Arrays.asList("Runnable", "Threadable", "Processable", "Executable"), "Runnable");
            addQuestion("Java", 4, "What does the `try-with-resources` statement do in Java?",
                    Arrays.asList("Allocates CPU cores", "Automatically closes AutoCloseable resources", "Optimizes memory", "Spawns worker threads"), "Automatically closes AutoCloseable resources");
            addQuestion("Java", 4, "What is autoboxing in Java?",
                    Arrays.asList("Automatic memory reclamation", "Automatic conversion between primitive types and their wrapper classes", "Automatic class loading", "Encapsulating fields"), "Automatic conversion between primitive types and their wrapper classes");
            addQuestion("Java", 4, "Which collection does NOT guarantee any specific ordering of its elements?",
                    Arrays.asList("TreeSet", "LinkedHashSet", "HashSet", "ArrayList"), "HashSet");
            addQuestion("Java", 4, "What is a functional interface in Java 8+?",
                    Arrays.asList("Interface with no methods", "Interface with exactly one abstract method", "Interface with only default methods", "Interface for mathematical operations"), "Interface with exactly one abstract method");
            addQuestion("Java", 4, "Which stream operation is an intermediate operation in Java Stream API?",
                    Arrays.asList("collect()", "forEach()", "filter()", "count()"), "filter()");
            addQuestion("Java", 4, "What is the access level of a member declared without any access modifier?",
                    Arrays.asList("public", "private", "protected", "package-private (default)"), "package-private (default)");

        } catch (Exception e) {
            System.err.println("Error seeding initial data: " + e.getMessage());
        }
    }

    private void addQuestion(String subject, int bank, String questionText, List<String> options, String correctAnswer) throws Exception {
        SubjectQuestion sq = SubjectQuestion.builder()
                .subject(subject)
                .bank(bank)
                .question(questionText)
                .optionsJson(objectMapper.writeValueAsString(options))
                .correctAnswer(correctAnswer)
                .build();
        subjectQuestionRepository.save(sq);
    }
}
