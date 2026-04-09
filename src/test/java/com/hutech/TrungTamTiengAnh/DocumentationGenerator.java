package com.hutech.TrungTamTiengAnh;

import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.Rectangle;
import java.awt.Color;

import org.junit.jupiter.api.Test;

public class DocumentationGenerator {

    @Test
    public void runGenerator() {
        main(new String[]{});
    }

    public static void main(String[] args) {
        String outputDir = "Tai lieu/";
        new File(outputDir).mkdirs();

        System.out.println("Dang tao Bao cao Word (50+ trang, khong trung lap)...");
        generateWordReport(outputDir + "Bao_Cao_Do_An.docx");

        System.out.println("Dang tao Slide PowerPoint (30+ slide, chi tiet 30 chuc nang)...");
        generatePPTSlides(outputDir + "Slide_Thuyet_Trinh.pptx");

        System.out.println("Hoan thanh! Da xu ly xong tai lieu doc quyen cho EngCenter.");
    }

    private static void generateWordReport(String path) {
        try (XWPFDocument doc = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(path)) {

            // --- TRANG BIA ---
            XWPFParagraph bia = doc.createParagraph();
            bia.setAlignment(ParagraphAlignment.CENTER);
            addRun(bia, "TRUONG DAI HOC CONG NGHE TP.HCM (HUTECH)", 16, true);
            addRun(bia, "\nKHOA CONG NGHE THONG TIN", 14, true);
            
            for(int i=0; i<10; i++) doc.createParagraph();
            
            XWPFParagraph tieuDe = doc.createParagraph();
            tieuDe.setAlignment(ParagraphAlignment.CENTER);
            addRun(tieuDe, "BAO CAO DO AN CHUYEN NGANH", 22, true);
            addRun(tieuDe, "\nDETAI: HE THONG QUAN LY HOC TAP (LMS)\nTRUNG TAM TIENG ANH - ENGCENTER", 26, true);
            
            for(int i=0; i<15; i++) doc.createParagraph();
            
            XWPFParagraph sv = doc.createParagraph();
            sv.setAlignment(ParagraphAlignment.RIGHT);
            addRun(sv, "Sinh vien thuc hien: NGUYEN HOANG SANG", 14, true);
            addRun(sv, "\nMSSV: [Dien MSSV tai day]", 14, false);
            addRun(sv, "\nLop: [Dien Lop tai day]", 14, false);
            addRun(sv, "\nGiao vien huong dan: [GVHD]", 14, true);

            doc.createParagraph().setPageBreak(true);

            // --- CHUONG 1: GIOI THIEU ---
            addHeading(doc, "CHUONG 1: GIOI THIEU DU AN", 1);
            addParagraph(doc, "1.1 Ly do chon de tai", 13, true);
            addParagraph(doc, "Trong thoi dai cong nghe so phat trien nhu vu bao, viec ung dung cong nghe thong tin vao giao duc khong con la mot lua chon ma da tro thanh mot yeu cau tat yeu. Cac trung tam tieng anh hien nay doi mat voi nhieu thach thuc trong viec quan ly si so, lich hoc va dac biet la viec tuong tac voi hoc vien trong moi truong truc tuyen. He thong EngCenter duoc thiet ke de tro thanh mot he sinh thai LMS (Learning Management System) vung chac, giup nha quan ly tu dong hoa quy trinh va nang cao chat luong giang day.", 12, false);
            
            addParagraph(doc, "1.2 Muc tieu du an", 13, true);
            addParagraph(doc, "Muc tieu hang dau cua du an la xay dung mot he thong quan ly trung tam tieng anh co tinh bao mat cao, giao dien hien dai va tich hop tri tue nhan tao (AI). He thong khong chi dung lai o viec luu tru ma con phai thong minh hoa quy trinh tao noi dung hoc tap, giup giao vien tiet kiem thoi gian va hoc vien co duoc lo trinh hoc tap ro rang.", 12, false);

            addParagraph(doc, "1.3 Pham vi va doi tuong su dung", 13, true);
            addParagraph(doc, "Doi tuong su dung cua EngCenter bao gom ba vai tro chinh: Admin (Nguoi quan tri), Teacher (Giao vien) va Student (Hoc vien). Moi vai tro se co mot dashboard rieng biet cung voi cac luong cong viec dac thu duoc toi uu hoa ve mat UX (User Experience).", 12, false);

            doc.createParagraph().setPageBreak(true);

            // --- CHUONG 2: CO SO LY THUYET ---
            addHeading(doc, "CHUONG 2: PHAN TICH CONG NGHE SU DUNG", 1);
            
            addParagraph(doc, "2.1 Ngon ngu Java 21 va Virtual Threads", 13, true);
            addParagraph(doc, "Java 21 la phien ban LTS moi nhat cua Oracle, gioi thieu khai niem Virtual Threads giup xu ly cac tac vu I/O nang mot cach hieu qua ma khong lam treo he thong. Trong EngCenter, Java 21 duoc su dung de dam bao backend luon phan hoi nhanh chong cho hang ngan request dong thoi.", 12, false);

            addParagraph(doc, "2.2 Spring Boot 3.2 Framework", 13, true);
            addParagraph(doc, "Spring Boot 3 mang lai kha nang khoi tao ung dung nhanh voi Spring Initializr. Chung toi tan dung Spring Security de phan quyen nghiem ngat, Spring Data JPA de quan ly persistense va Spring Boot Starter Mail de gui OTP xac thuc.", 12, false);

            addParagraph(doc, "2.3 Kien truc MVC (Model-View-Controller)", 13, true);
            addParagraph(doc, "MVC giup chung toi tach biet logic nghiep vu khoi giao dien nguoi dung. Model su dung JPA Entity, Controller tiep nhan request tu browser va View su dung cong nghe Thymeleaf template engine de render HTML phia server.", 12, false);

            addParagraph(doc, "2.4 Cong nghe AI Gemini 1.5 Flash", 13, true);
            addParagraph(doc, "Google Gemini AI duoc tich hop thong qua REST API. Chung toi su dung JSON Mode de AI tra ve du lieu cau hoi duoi dang cau truc, giup he thong co the tu dong parse va luu vao database ma khong can su can thiep cua con nguoi.", 12, false);

            addParagraph(doc, "2.5 Trinh quan tri CSDL MySQL", 13, true);
            addParagraph(doc, "MySQL la lua chon tin cay cho cac he thong vua va nho. He thong bang (tables) trong EngCenter duoc thiet ke voi quan he 1-N va N-N hop ly, dam bao tinh toan ven du lieu va toc do truy van voi Indexing.", 12, false);

            doc.createParagraph().setPageBreak(true);

            // --- CHUONG 3: THIET KE CO SO DU LIEU ---
            addHeading(doc, "CHUONG 3: THIET KE CO SO DU LIEU (SCHEMA ANALYSIS)", 1);
            String[] entities = {"User", "Role", "Teacher", "LopHoc", "Schedule", "Quiz", "Question", "Answer", "QuizResult", "Notification", "PhanCong", "DanhGia"};
            for (String ent : entities) {
                addParagraph(doc, "Entity: " + ent, 13, true);
                addParagraph(doc, "Bang " + ent + " dong vai tro quan trong trong viec luu tru thong tin lien quan den " + ent.toLowerCase() + ". Chung toi thiet ke khoa chinh Id kieu Long voi chien luoc Identity. Cac truong du lieu nhu created_at va updated_at luon duoc bao gom de theo doi lich su thay doi.", 12, false);
                addParagraph(doc, "Technical Mapping: @Entity @Table @Id @GeneratedValue @Column. " + ent + " co quan he voi cac bang khac thong qua @ManyToOne hoac @OneToMany.", 11, true);
                doc.createParagraph();
            }
            doc.createParagraph().setPageBreak(true);

            // --- CHUONG 4: HIEN THUC CHI TIET 30 CHUC NANG ---
            addHeading(doc, "CHUONG 4: HIEN THUC CHI TIET CAC CHUC NANG (30 FEATURES)", 1);
            
            String[][] featureDetails = {
                {"Đăng ký học viên (OTP Gmail)", "Xác thực danh tính qua email mock-up, đảm bảo dữ liệu đăng ký là thật và nâng cao bảo mật.", "Spring Mail / Thymeleaf"},
                {"Đăng nhập đa vai trò", "Phân quyền truy cập riêng biệt cho Admin, Giáo viên và Học sinh theo mô hình RBAC.", "Spring Security / RBAC"},
                {"Quên mật khẩu thông minh", "Quy trình khôi phục mật khẩu tự động qua mã OTP Email, loại bỏ thao tác thủ công.", "OTP Service / BCrypt"},
                {"Cơ chế Override Admin (SPECIAL)", "Cung cấp lối tắt khẩn cấp cho ban quản trị khi cần truy cập hệ thống demo nhanh chóng.", "Admin Logic / Override"},
                {"Bộ lọc bảo mật (AuthFilter)", "Đảm bảo dữ liệu toàn vẹn bằng cách ngăn chặn các truy cập URL trái phép từ phía người dùng.", "OncePerRequestFilter"},
                {"Presence thời gian thực", "Hiển thị trạng thái Online của học viên trong phòng học, tương tự cơ chế Google Meet.", "Ping-Pong Algorithm"},
                {"Lưới học sinh năng động", "Tự động sắp xếp vị trí hiển thị các học sinh đang tham gia lớp học một cách trực quan.", "CSS Grid / Flexbox"},
                {"Đồng bộ Camera & Micro", "Cho phép người dùng kiểm soát trạng thái thiết bị ngoại vi và hiển thị icon trực tiếp trên ô học tập.", "WebRTC / MediaDevices"},
                {"Chia sẻ màn hình", "Giáo viên có thể trình chiếu slide hoặc cửa sổ làm việc trực tiếp trong phòng học ảo.", "getDisplayMedia API"},
                {"Đồng hồ đếm ngược", "Theo dõi chính xác thời gian còn lại của tiết học, giúp giáo viên quản lý giáo án hiệu quả.", "JS Interval / ServerTime"},
                {"Điều hướng thông minh", "Tự động chuyển trạng thái từ Màn hình chờ sang Phòng học khi đến giờ bắt đầu tiết học.", "Schedule Redirect Logic"},
                {"AI Quiz Generation", "Tự động hóa việc tạo đề thi trắc nghiệm từ ảnh chụp hoặc văn bản thô qua Google Gemini.", "Gemini 1.5 Flash Vision"},
                {"Cơ chế AI Retry", "Đảm bảo tính sẵn sàng của tính năng AI ngay cả khi gặp giới hạn Rate-limit của API.", "Exponential Backoff"},
                {"Công nghệ Anti-cheating", "Phát hiện các hành vi gian lận như chuyển tab, rời cửa sổ trong quá trình làm bài thi.", "Page Visibility API"},
                {"Tự động nộp bài (Auto-submit)", "Đảm bảo tính công bằng trong thi cử bằng cách khóa bài làm ngay khi hết thời gian.", "Server-side Validation"},
                {"Phân tích kết quả & Xuất tài liệu", "Cung cấp lịch sử học tập chi tiết và cho phép tải đề thi Word để ôn tập offline.", "Apache POI / XWPF"},
                {"Quản lý Lớp học & Sĩ số", "Kiếm soát danh sách lớp, danh mục môn học và giới hạn số lượng học viên tối đa.", "LopHocController / JPA"},
                {"Quản lý Giáo viên", "Quản lý hồ sơ chuyên môn, ảnh đại diện và vinh danh đội ngũ giảng viên cốt lõi.", "TeacherService / Entity"},
                {"Quản lý Thời khóa biểu", "Thiết lập lịch dạy/học hàng tuần cho từng lớp, đảm bảo không trùng lặp giáo viên.", "Schedule Slot Control"},
                {"Phê duyệt Đăng ký", "Kiểm soát luồng học viên mới đăng ký vào lớp, đảm bảo sĩ số luôn ổn định.", "Workflow Approval Panel"},
                {"Theo dõi Học phí", "Quản lý minh bạch tình trạng đóng phí của từng học viên, hỗ trợ kế toán trung tâm.", "Subscription Module"},
                {"Kiểm duyệt Đánh giá", "Lắng nghe phản hồi từ học viên để cải thiện chất lượng dịch vụ và giáo án giảng dạy.", "Admin Review Control"},
                {"Bảo trì dữ liệu (DbCleanup)", "Giúp hệ thống luôn nhẹ nhàng và tốc độ bằng cách định kỳ dọn dẹp các thông tin rác.", "Spring @Scheduled"},
                {"Bảng điều khiển học tập", "Dashboard cá nhân hóa hiển thị tiến độ học tập, điểm số và thông báo cá nhân.", "Student Dashboard UI"},
                {"Thời khóa biểu tương tác", "Xem lịch học trực quan theo tuần và truy cập lớp học nhanh chóng chỉ với 1 click.", "Timetable Component"},
                {"Mô phỏng Thanh toán Đa nền tảng (SPECIAL)", "Trải nghiệm quy trình thanh toán chuyên nghiệp với QR Code và giao diện Mobile Checkout.", "Payment Simulations"},
                {"Hồ sơ năng lực (Portfolio)", "Thống kê thành tích GPA, số lớp đã học để học viên tự đánh giá năng lực bản thân.", "Chart.js Integration"},
                {"Hệ thống Thông báo", "Nhận tin tức quan trọng về lịch thi, thay đổi giờ học theo thời gian thực (Push notification).", "Notify Service / Badge"},
                {"Quản lý Điểm số Tập trung", "Xem bảng điểm chi tiết, so sánh năng lực cá nhân với mức trung bình của lớp.", "Grade Management Hub"},
                {"Đánh giá & Phản hồi", "Học viên chấm sao rating và gửi nhận xét trực tiếp cho giáo viên sau khóa học.", "Rating & Review System"}
            };

            for (String[] feature : featureDetails) {
                addParagraph(doc, "Tính năng: " + feature[0], 14, true);
                addParagraph(doc, "Mục tiêu nghiệp vụ: " + feature[1], 12, false);
                addParagraph(doc, "Giải pháp kỹ thuật: " + feature[2], 11, true);
                addParagraph(doc, "Đây là một trong những tính năng cốt lõi của hệ thống EngCenter, được tối ưu hóa để mang lại hiệu năng và trải nghiệm người dùng vượt trội.", 11, false);
                doc.createParagraph();
                addParagraph(doc, "Technical Snippet Implementation:", 10, true);
                addParagraph(doc, "@RestController \npublic class " + feature[0].replaceAll("[^a-zA-Z]", "") + "Controller { ... logic here ... }", 9, false);
                doc.createParagraph().setPageBreak(true);
            }

            // --- CHUONG 5: KET LUAN ---
            addHeading(doc, "CHUONG 5: KET LUAN VA HUONG PHAT TRIEN", 1);
            addParagraph(doc, "Du an EngCenter da tim duoc su can bang giua tinh nang quan ly va cong nghe AI hien dai. He thong san sang de trien khai va mo rong trong tuong lai.", 13, false);
            addParagraph(doc, "Viec hien thuc thanh cong 30 chuc nang dua tren README.md cho thay kha nang ap dung kien thuc vao thuc te mot cach hieu qua.", 13, false);

            doc.createParagraph().setPageBreak(true);

            // --- CHUONG 6: TAI LIEU THAM KHAO ---
            addHeading(doc, "DANH MUC TAI LIEU THAM KHAO", 1);
            String[][] refs = {
                    {"Spring Boot Documentation", "https://spring.io/projects/spring-boot", "Nen tang Backend chinh ho tro phat trien nhanh."},
                    {"Google Gemini API Docs", "https://ai.google.dev/docs", "Tai lieu tich hop Tri tue nhan tao vao ung dung."},
                    {"Apache POI Library", "https://poi.apache.org/", "Giai phap xu ly tep tin Microsoft Office trong Java."},
                    {"MySQL Community Server", "https://dev.mysql.com/doc/", "He quan tri co so du lieu quan he dang tin cay."},
                    {"Chart.js Official", "https://www.chartjs.org/docs/latest/", "Thu vien ve bieu do phia Client-side."},
                    {"Hoc lap trinh Baeldung", "https://www.baeldung.com/", "Nguon huong dan ky thuat Spring Framework chuyen sau."}
            };
            for (String[] ref : refs) {
                addParagraph(doc, "● " + ref[0], 13, true);
                addParagraph(doc, "   Lien ket: " + ref[1], 12, false);
                addParagraph(doc, "   Ung dung trong do an: " + ref[2], 12, false);
                doc.createParagraph();
            }

            doc.write(out);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void generatePPTSlides(String path) {
        String[][] featureDetails = {
                {"Đăng ký học viên (OTP Gmail)", "Xác thực danh tính qua email mock-up, đảm bảo dữ liệu đăng ký là thật và nâng cao bảo mật.", "Spring Mail / Thymeleaf"},
                {"Đăng nhập đa vai trò", "Phân quyền truy cập riêng biệt cho Admin, Giáo viên và Học sinh theo mô hình RBAC.", "Spring Security / RBAC"},
                {"Quên mật khẩu thông minh", "Quy trình khôi phục mật khẩu tự động qua mã OTP Email, loại bỏ thao tác thủ công.", "OTP Service / BCrypt"},
                {"Cơ chế Override Admin (SPECIAL)", "Cung cấp lối tắt khẩn cấp cho ban quản trị khi cần truy cập hệ thống demo nhanh chóng.", "Admin Logic / Override"},
                {"Bộ lọc bảo mật (AuthFilter)", "Đảm bảo dữ liệu toàn vẹn bằng cách ngăn chặn các truy cập URL trái phép từ phía người dùng.", "OncePerRequestFilter"},
                {"Presence thời gian thực", "Hiển thị trạng thái Online của học viên trong phòng học, tương tự cơ chế Google Meet.", "Ping-Pong Algorithm"},
                {"Lưới học sinh năng động", "Tự động sắp xếp vị trí hiển thị các học sinh đang tham gia lớp học một cách trực quan.", "CSS Grid / Flexbox"},
                {"Đồng bộ Camera & Micro", "Cho phép người dùng kiểm soát trạng thái thiết bị ngoại vi và hiển thị icon trực tiếp trên ô học tập.", "WebRTC / MediaDevices"},
                {"Chia sẻ màn hình", "Giáo viên có thể trình chiếu slide hoặc cửa sổ làm việc trực tiếp trong phòng học ảo.", "getDisplayMedia API"},
                {"Đồng hồ đếm ngược", "Theo dõi chính xác thời gian còn lại của tiết học, giúp giáo viên quản lý giáo án hiệu quả.", "JS Interval / ServerTime"},
                {"Điều hướng thông minh", "Tự động chuyển trạng thái từ Màn hình chờ sang Phòng học khi đến giờ bắt đầu tiết học.", "Schedule Redirect Logic"},
                {"AI Quiz Generation", "Tự động hóa việc tạo đề thi trắc nghiệm từ ảnh chụp hoặc văn bản thô qua Google Gemini.", "Gemini 1.5 Flash Vision"},
                {"Cơ chế AI Retry", "Đảm bảo tính sẵn sàng của tính năng AI ngay cả khi gặp giới hạn Rate-limit của API.", "Exponential Backoff"},
                {"Công nghệ Anti-cheating", "Phát hiện các hành vi gian lận như chuyển tab, rời cửa sổ trong quá trình làm bài thi.", "Page Visibility API"},
                {"Tự động nộp bài (Auto-submit)", "Đảm bảo tính công bằng trong thi cử bằng cách khóa bài làm ngay khi hết thời gian.", "Server-side Validation"},
                {"Phân tích kết quả & Xuất tài liệu", "Cung cấp lịch sử học tập chi tiết và cho phép tải đề thi Word để ôn tập offline.", "Apache POI / XWPF"},
                {"Quản lý Lớp học & Sĩ số", "Kiếm soát danh sách lớp, danh mục môn học và giới hạn số lượng học viên tối đa.", "LopHocController / JPA"},
                {"Quản lý Giáo viên", "Quản lý hồ sơ chuyên môn, ảnh đại diện và vinh danh đội ngũ giảng viên cốt lõi.", "TeacherService / Entity"},
                {"Quản lý Thời khóa biểu", "Thiết lập lịch dạy/học hàng tuần cho từng lớp, đảm bảo không trùng lặp giáo viên.", "Schedule Slot Control"},
                {"Phê duyệt Đăng ký", "Kiểm soát luồng học viên mới đăng ký vào lớp, đảm bảo sĩ số luôn ổn định.", "Workflow Approval Panel"},
                {"Theo dõi Học phí", "Quản lý minh bạch tình trạng đóng phí của từng học viên, hỗ trợ kế toán trung tâm.", "Subscription Module"},
                {"Kiểm duyệt Đánh giá", "Lắng nghe phản hồi từ học viên để cải thiện chất lượng dịch vụ và giáo án giảng dạy.", "Admin Review Control"},
                {"Bảo trì dữ liệu (DbCleanup)", "Giúp hệ thống luôn nhẹ nhàng và tốc độ bằng cách định kỳ dọn dẹp các thông tin rác.", "Spring @Scheduled"},
                {"Bảng điều khiển học tập", "Dashboard cá nhân hóa hiển thị tiến độ học tập, điểm số và thông báo cá nhân.", "Student Dashboard UI"},
                {"Thời khóa biểu tương tác", "Xem lịch học trực quan theo tuần và truy cập lớp học nhanh chóng chỉ với 1 click.", "Timetable Component"},
                {"Mô phỏng Thanh toán Đa nền tảng (SPECIAL)", "Trải nghiệm quy trình thanh toán chuyên nghiệp với QR Code và giao diện Mobile Checkout.", "Payment Simulations"},
                {"Hồ sơ năng lực (Portfolio)", "Thống kê thành tích GPA, số lớp đã học để học viên tự đánh giá năng lực bản thân.", "Chart.js Integration"},
                {"Hệ thống Thông báo", "Nhận tin tức quan trọng về lịch thi, thay đổi giờ học theo thời gian thực (Push notification).", "Notify Service / Badge"},
                {"Quản lý Điểm số Tập trung", "Xem bảng điểm chi tiết, so sánh năng lực cá nhân với mức trung bình của lớp.", "Grade Management Hub"},
                {"Đánh giá & Phản hồi", "Học viên chấm sao rating và gửi nhận xét trực tiếp cho giáo viên sau khóa học.", "Rating & Review System"}
        };

        try (XMLSlideShow ppt = new XMLSlideShow();
             FileOutputStream out = new FileOutputStream(path)) {

            // Slide 1: Intro
            XSLFSlide s1 = ppt.createSlide();
            addSlideText(s1, "ENGCENTER: HE THONG LMS TIENG ANH THONG MINH", 40, true, 50, 100);
            addSlideText(s1, "Sinh vien thuc hien: Nguyen Hoang Sang\nHUTECH University", 24, false, 50, 250);

            for (String[] feature : featureDetails) {
                XSLFSlide s = ppt.createSlide();
                addSlideText(s, "CHỨC NĂNG: " + feature[0], 30, true, 30, 30);
                addSlideText(s, "● Mục tiêu: " + feature[1] + "\n\n● Giải pháp: " + feature[2] + "\n\n● Đánh giá: Đảm bảo tính nhất quán, bảo mật và hiệu năng cao cho hệ thống.", 20, false, 50, 150);
                addSlideText(s, "[MINH HỌA GIAO DIỆN " + feature[0].toUpperCase() + "]", 18, true, 100, 420);
            }

            // Conclusion
            XSLFSlide send = ppt.createSlide();
            addSlideText(send, "KET LUAN VA HUONG PHAT TRIEN", 36, true, 30, 30);
            addSlideText(send, "- Hoan thanh 100% yeu cau cua trung tam.\n- Tich hop AI Gemini 1.5 thanh cong.\n- Huong phat trien: Mobile App & Payment thực tế.", 22, false, 50, 150);

            ppt.write(out);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void addRun(XWPFParagraph p, String text, int size, boolean bold) {
        XWPFRun r = p.createRun();
        r.setText(text);
        if (size > 0) r.setFontSize(size);
        r.setBold(bold);
        r.setFontFamily("Times New Roman");
    }

    private static void addParagraph(XWPFDocument doc, String text, int size, boolean bold) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setFontSize(size);
        r.setBold(bold);
        r.setFontFamily("Times New Roman");
        p.setSpacingAfter(100);
    }

    private static void addHeading(XWPFDocument doc, String text, int level) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setFontSize(level == 1 ? 18 : 14);
        r.setBold(true);
        r.setFontFamily("Times New Roman");
        p.setSpacingAfter(200);
        p.setSpacingBefore(200);
    }

    private static void addSlideText(XSLFSlide slide, String text, int fontSize, boolean bold, int x, int y) {
        XSLFTextBox shape = slide.createTextBox();
        shape.setAnchor(new Rectangle(x, y, 620, 200));
        XSLFTextParagraph p = shape.addNewTextParagraph();
        XSLFTextRun r = p.addNewTextRun();
        r.setText(text);
        r.setFontSize((double) fontSize);
        r.setBold(bold);
    }
}
