package com.projectx.codeecho.service.serviceImpl;

import com.projectx.codeecho.CodeEchoApplication;
import com.projectx.codeecho.domain.dto.CompileResponse;
import com.projectx.codeecho.service.CompileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Component
public class CompileServiceImpl implements CompileService {
    private final String path = "./";

    public Object compileCode(String body) {
        String uuid = UUID.randomUUID().toString().replace("-","");
        String uuidPath = path + uuid + "/";

        // Source를 이용한 java file 생성
        File newFolder = new File(uuidPath);
        File sourceFile = new File(uuidPath + "DynamicClass.java");
        File classFile = new File(uuidPath + "DynamicClass.class");

        Class<?> cls = null;

        // compile System err console 조회용 변수
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream origErr = System.err;

        try {
            newFolder.mkdir();
            new FileWriter(sourceFile).append(body).close();

            // 만들어진 Java 파일을 컴파일
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            // System의 error outputStream을 ByteArrayOutputStream으로 받아오도록 설정
            System.setErr(new PrintStream(err));

            // compile 진행
            int compileResult = compiler.run(null, null, null, sourceFile.getPath());
            // compile 실패인 경우 에러 로그 반환
            if(compileResult == 1) {
                return err.toString();
            }

            // 컴파일된 Class를 Load
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {new File(uuidPath).toURI().toURL()});
            cls = Class.forName("DynamicClass", true, classLoader);

            // Load한 Class의 Instance를 생성
            return cls.newInstance();

        } catch (Exception e) {
            log.error("[CompileBuilder] 소스 컴파일 중 에러 발생 :: {}", e.getMessage());
            e.printStackTrace();
            return null;

        } finally {
            // Syetem error stream 원상태로 전환
            System.setErr(origErr);

            if(sourceFile.exists())
                sourceFile.delete();
            if(classFile.exists())
                classFile.delete();
            if(newFolder.exists())
                newFolder.delete();
        }
    }

    // run method : parameter byte array, return byte array
    public Map<String, Object> runObject(Object obj) {
        // 실행 중에 출력되는 스트림을 변수로 저장하고 싶을 때
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        PrintStream origErr = System.err;

        Map<String, Object> returnMap = new HashMap<>();

        try {
            // System의 out, error outputStream을 ByteArrayOutputStream으로 받아오도록 설정
            System.setOut(new PrintStream(out));
            System.setErr(new PrintStream(err));

            // invoke() 함수가 실행 중에 System.out 출력물을 변수로 'out' 변수에 저장함.
            // 메소드 timeout을 체크하며 실행(15초 초과 시 강제종료)
            Map<String, Object> result = MethodExecutation.timeOutCall(obj);

            // stream 정보 저장
            boolean b = err.toString() != null && !err.toString().equals("");
            if((Boolean) result.get("result")) {
                returnMap.put("result", CompileResponse.SUCEESS.getText());
                returnMap.put("return", result.get("return"));
                if(b) {
                    returnMap.put("SystemOut", err.toString().replace("\n", ""));
                }else {
                    returnMap.put("SystemOut", out.toString().replace("\n", ""));
                }
            }else {
                returnMap.put("result", CompileResponse.FAIL.getText());
                if(b) {
                    returnMap.put("SystemOut", err.toString().replace("\n", ""));
                }else {
                    returnMap.put("SystemOut", "제한 시간 초과");
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            // Syetem out, error stream 원상태로 전환
            System.setOut(origOut);
            System.setErr(origErr);
        }
        return returnMap;
    }

    public static class MethodExecutation {
        private final static long TIMEOUT_LONG = 15000; // 15초

        public static Map<String, Object> timeOutCall(Object obj) throws Exception {
            // return Map
            Map<String, Object> returnMap = new HashMap<>();

            // Source를 만들때 지정한 Method
            Method objMethod = obj.getClass().getMethod("main", String[].class);

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            Future<Map<String, Object>> future = executorService.submit(() -> {
                Map<String, Object> callMap = new HashMap<>();

                callMap.put("return", objMethod.invoke(obj, (Object) new String[] {}));

                callMap.put("result", true);
                return callMap;
            });

            try {
                // 타임아웃 감시할 작업 실행
                returnMap = future.get(TIMEOUT_LONG, TimeUnit.MILLISECONDS); // timeout을 설정
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                // e.printStackTrace();
                returnMap.put("result", false);
            } finally {
                executorService.shutdown();
            }

            return returnMap;
        }
    }

}
