(deftemplate fz_duration 0 300 s
    (
        (short (0 1) (50 1) (100 0) )
        (medium (50 0) (100 1) (160 1) (200 0) )
        (long (160 0) (200 1) (300 1) )
    )
)

(plot-fuzzy-value t "snl" nil nil
    (create-fuzzy-value fz_duration short)
    (create-fuzzy-value fz_duration medium)
    (create-fuzzy-value fz_duration long)
)

(deftemplate fz_mood 0 10 point
    (
        (sad (z 2 6) )
        (average (pi 3 5 ) )
        (cheerful (s 7 9) )
    )
)

(plot-fuzzy-value t "lmh" nil nil
    (create-fuzzy-value fz_mood sad)
    (create-fuzzy-value fz_mood average)
    (create-fuzzy-value fz_mood cheerful)
)

(deftemplate fz_rating 0 15 star
    (
        (low (z 1 4) )
        (medium (pi 4 10 ) )
        (high (s 14 15 ) )
    )
)

(plot-fuzzy-value t "lmh" nil nil
    (create-fuzzy-value fz_rating low)
    (create-fuzzy-value fz_rating medium)
    (create-fuzzy-value fz_rating high)
)

(deftemplate music_composition
    (slot duration (type FUZZY-VALUE fz_duration) )
    (slot mood (type FUZZY-VALUE fz_mood) )
    (slot rating (type FUZZY-VALUE fz_rating) )
)

(defrule rule1 (music_composition (duration short))=>(printout t  "music composition is short" crlf))

(defrule rule2 (music_composition (duration medium))=>(printout t  "music composition is medium" crlf))

(defrule rule3 (music_composition (duration long))=>(printout t  "music composition is long" crlf))

(defrule rule4 (music_composition (mood sad))=>(printout t  "music composition is sad" crlf))

(defrule rule5 (music_composition (mood average))=>(printout t  "music composition is average" crlf))

(defrule rule6 (music_composition (mood cheerful))=>(printout t  "music composition is cheerful" crlf))

(defrule rule7 (music_composition (rating low))=>(printout t  "music composition rating is low" crlf))

(defrule rule8 (music_composition (rating medium))=>(printout t  "music composition rating is medium" crlf))

(defrule rule9 (music_composition (rating high))=>(printout t  "music composition rating is long" crlf))

(defrule fuzzify
    (duration ?d) (mood ?m) (rating ?r)
    =>
    (assert
        (music_composition
            (duration (?d 0) (?d 1) (?d 0) )
            (mood(?m 0) (?m 1) (?m 0) )
            (rating (?r 0) (?r 1) (?r 0) )
        )
    )
)

(assert (music_composition
  (duration (0 1) (50 1) (100 0))
  (mood (2 1) (6 0))
  (rating (1 1) (4 0))
))

(assert (music_composition
  (duration (50 0) (100 1) (160 1) (200 0))
  (mood (7 0) (8 1) (9 1))
  (rating (4 0) (10 1))
))

(assert (music_composition
  (duration (160 0) (200 1) (300 1))
  (mood (3 0) (4 1) (5 0))
  (rating (14 0) (15 1))
))

(assert (duration 1))

(assert (mood 1))

(assert (rating 1))

(run)

(deftemplate fz_rating2 0 15 star
    (
        (medium (pi 4 10 ) )
        (low below medium)
        (high above medium)
    )
)

(plot-fuzzy-value t "lmh" nil nil
    (create-fuzzy-value fz_rating2 low)
    (create-fuzzy-value fz_rating2 medium)
    (create-fuzzy-value fz_rating2 high)
)

(exit)
