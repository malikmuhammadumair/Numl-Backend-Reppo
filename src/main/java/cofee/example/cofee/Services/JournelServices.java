package cofee.example.cofee.Services;

import cofee.example.cofee.Entity.JournelEntries;
import cofee.example.cofee.Entity.UserEntity;
import cofee.example.cofee.Repository.JounelRepository;
import cofee.example.cofee.dto.FilterRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import com.cloudinary.Cloudinary;

import java.util.List;
import java.util.Map;

@Service
public class JournelServices {

    @Autowired
    private JounelRepository coffeeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private Cloudinary cloudinary;

    // Save new journal entry
    @Transactional
    public JournelEntries saveEntry(JournelEntries journelEntries, String username) {
        UserEntity user = userService.findbyusername(username);
        if (user == null) throw new RuntimeException("User not found: " + username);

        journelEntries.setUser(user);
        JournelEntries saved = coffeeRepository.save(journelEntries);
        user.getPapers().add(saved);
        userService.PostAll(user);
        return saved;
    }

    // Update journal entry
    public JournelEntries updateEntry(JournelEntries journelEntries) {
        return coffeeRepository.save(journelEntries);
    }

    // Delete journal by id
    @Transactional
    public boolean deleteById(Long id, String username) {
        UserEntity user = userService.findbyusername(username);
        boolean removed = user.getPapers().removeIf(x -> x.getId().equals(id));
        if (!removed) return false;

        userService.PostAll(user);
        coffeeRepository.deleteById(id);
        return true;
    }

    // Save journal entry with image (Cloudinary)
    public JournelEntries saveWithImage(JournelEntries entry, MultipartFile file, String username) {
        try {
            if (file == null || file.isEmpty())
                throw new RuntimeException("Image file is required");

            UserEntity user = userService.findbyusername(username);
            if (user == null) throw new RuntimeException("User not found: " + username);

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of("folder", "uploads"));

            entry.setPublicId((String) uploadResult.get("public_id"));
            entry.setUrl((String) uploadResult.get("secure_url"));
            entry.setUser(user);

            JournelEntries saved = coffeeRepository.save(entry);
            user.getPapers().add(saved);
            userService.PostAll(user);
            return saved;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save journal entry", e);
        }
    }

    // Get all journal entries
    public List<JournelEntries> getAll() {
        return coffeeRepository.findAll();
    }

    // Filter journals based on request
    public List<JournelEntries> filter(FilterRequest req) {
        return coffeeRepository.filterEntries(
                req.getDepartment(),
                req.getProgram(),
                req.getCourse(),
                req.getSemester(),
                req.getYear(),
                req.getExamtype()
        );
    }

    // Find journal by id
    public JournelEntries findJournalById(Long id) {
        return coffeeRepository.findById(id).orElse(null);
    }

    // Save journal (generic)
    public JournelEntries saveJournal(JournelEntries entry) {
        return coffeeRepository.save(entry);
    }

    // Get recent 6 approved journals (no file data)
    public List<JournelEntries> getRecent6() {
        return coffeeRepository.findRecentApproved(PageRequest.of(0, 6));
    }

    // Get trending 6 journals (no file data)
    public List<JournelEntries> getTrending6() {
        return coffeeRepository.findTrendingApproved(PageRequest.of(0, 6));
    }

    // Count papers uploaded by user
    public Long getPapersByUsername(String username) {
        return coffeeRepository.countUploadedPapersByUsername(username);
    }

    // Count approved papers by user
    public Long countApprovedPapers(String username) {
        return coffeeRepository.countApprovedPapersByUsername(username);
    }

    // Count pending papers by user
    public Long countPendingPapers(String username) {
        return coffeeRepository.countPendingPapersByUsername(username);
    }
}