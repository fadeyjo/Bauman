(deftemplate fz_interesting 0 20 point
    (
        (not_interesting (0 1) (6 1) (9 0) )
        (medium (6 0) (9 1) (13 1) (15 0) )
        (very_interesting (13 0) (15 1) (20 1) )
    )
)

(deftemplate fz_raiting 0 15 star
    (
        (low (0 1) (3 1) (7 0) )
        (medium (3 0) (7 1) (9 1) (13 0) )
        (high (9 0) (13 1) (15 1) )
    )
)

(defrule rule1
    (fz_interesting not_interesting) => (assert (fz_raiting low))
)

(defrule rule2
    (fz_interesting medium) => (assert (fz_raiting medium))
)

(defrule rule3
    (fz_interesting very_interesting) => (assert (fz_raiting high))
)

(defrule show
    ?result <- (fz_raiting ?state)
    =>
    (if (neq ?result nil) then
        (plot-fuzzy-value t "*" nil nil ?result)
        (printout t "===> Тип метода вывода: " (get-fuzzy-inference-type) crlf)
        (printout t "===> Defuzzification:" crlf)
        (printout t " COG algorithm: " (moment-defuzzify ?result) crlf)
        (printout t " MOM algorithm: " (maximum-defuzzify ?result) crlf)
    else
        (printout t "No facts there, this is error" crlf)
    )
)

(set-fuzzy-inference-type max-min)
(assert (fz_interesting (pi 0 14)))

(run)