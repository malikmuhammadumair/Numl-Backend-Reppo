package cofee.example.cofee.Repository;

import cofee.example.cofee.Entity.JournelEntries;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JounelRepository extends JpaRepository<JournelEntries, Long> {

    // ✅ Dynamic Filter (Only Approved Papers)
    @Query("""
        SELECT j FROM JournelEntries j
        WHERE j.status = 'approved'
        AND (:department IS NULL OR LOWER(j.department) = LOWER(:department))
        AND (:program IS NULL OR LOWER(j.program) = LOWER(:program))
        AND (:course IS NULL OR LOWER(j.course) = LOWER(:course))
        AND (:semester IS NULL OR LOWER(j.semester) = LOWER(:semester))
        AND (:year IS NULL OR LOWER(j.year) = LOWER(:year))
        AND (:examtype IS NULL OR LOWER(j.examtype) = LOWER(:examtype))
        ORDER BY j.id DESC
    """)
    List<JournelEntries> filterEntries(
            @Param("department") String department,
            @Param("program") String program,
            @Param("course") String course,
            @Param("semester") String semester,
            @Param("year") String year,
            @Param("examtype") String examtype
    );


    // ✅ Recent Approved Papers (Limit from DB)
    @Query("""
        SELECT j FROM JournelEntries j
        WHERE j.status = 'approved'
        ORDER BY j.id DESC
    """)
    List<JournelEntries> findRecentApproved(Pageable pageable);


    // ✅ Trending Approved Papers (Limit from DB)
    @Query("""
        SELECT j FROM JournelEntries j
        WHERE j.trending = true
        AND j.status = 'approved'
        ORDER BY j.id DESC
    """)
    List<JournelEntries> findTrendingApproved(Pageable pageable);


    // ✅ All Trending (if needed without limit)
    @Query("""
        SELECT j FROM JournelEntries j
        WHERE j.trending = true
        AND j.status = 'approved'
        ORDER BY j.id DESC
    """)
    List<JournelEntries> findAllTrending();


    // ✅ Count Total Uploaded Papers By User
    @Query("""
        SELECT COUNT(j)
        FROM JournelEntries j
        WHERE j.user.email = :username
    """)
    Long countUploadedPapersByUsername(@Param("username") String username);


    // ✅ Count Approved Papers By User
    @Query("""
        SELECT COUNT(j)
        FROM JournelEntries j
        WHERE j.user.email = :username
        AND j.status = 'approved'
    """)
    Long countApprovedPapersByUsername(@Param("username") String username);


    // ✅ Count Pending Papers By User
    @Query("""
        SELECT COUNT(j)
        FROM JournelEntries j
        WHERE j.user.email = :username
        AND j.status = 'pending'
    """)
    Long countPendingPapersByUsername(@Param("username") String username);


    // ✅ Find All Papers By Username (Admin / Profile Page)
    @Query("""
        SELECT j
        FROM JournelEntries j
        WHERE j.user.email = :username
        ORDER BY j.id DESC
    """)
    List<JournelEntries> findAllByUsername(@Param("username") String username);


    // ✅ Find All Pending (Admin Approval Page)
    @Query("""
        SELECT j
        FROM JournelEntries j
        WHERE j.status = 'pending'
        ORDER BY j.id DESC
    """)
    List<JournelEntries> findAllPending();


    // ✅ Find All Approved (Admin View)
    @Query("""
        SELECT j
        FROM JournelEntries j
        WHERE j.status = 'approved'
        ORDER BY j.id DESC
    """)
    List<JournelEntries> findAllApproved();

    @Query("SELECT j FROM JournelEntries j WHERE j.status = 'PENDING'")
    List<JournelEntries> findPendingPapers();
}