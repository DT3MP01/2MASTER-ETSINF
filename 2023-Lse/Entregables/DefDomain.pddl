(define (domain driverlog)
  (:requirements :typing :durative-actions :fluents)
  (:types
    city - object
    house station airport - location
    locatable transport - movables
    driver parcel - locatable
    truck plane train - transport
  )

  (:predicates
    (located ?loc - location ?city - city)
    (at ?obj - movables ?loc - location)
    (path ?x ?y - location)
    (loaded ?plc - parcel ?trs - transport)
    (driving ?d - driver ?v - transport)
  )

  (:functions
    (time-to-walk ?loc ?loc1 - location)
    (time-to-drive ?loc ?loc1 - location)
    (time-to-fly ?loc ?loc1 - airport)
    (time-to-train ?loc ?loc1 - station)
    (capacity-weight ?truck - truck)
    (parcel-weight ?pkg - parcel)
  )

  (:durative-action WALK
    :parameters (?driver - driver ?loc-from - location ?loc-to - location ?city - city
    )
    :duration (= ?duration (time-to-walk ?loc-from ?loc-to))
    :condition (and (at start (at ?driver ?loc-from))
      (at start (located ?loc-from ?city))
      (at start (located ?loc-to ?city))
      (over all (path ?loc-from ?loc-to))
    )
    :effect (and (at start (not (at ?driver ?loc-from)))
      (at end (at ?driver ?loc-to))
    )
  )

  (:durative-action MOVE-TRAIN
    :parameters (
       ?train - train ?loc-from - station ?loc-to - station
    )
    :duration (= ?duration (time-to-train ?loc-from ?loc-to))
    :condition (and
      (at start (at ?train ?loc-from))
      (over all (path ?loc-from ?loc-to))
    )
    :effect (and
      (at start (not (at ?train ?loc-from)))
      (at end (at ?train ?loc-to))
    )
  )

  (:durative-action LOAD-TRAIN
    :parameters (?pkg - parcel ?train - train ?loc - station)
    :duration (= ?duration 1)
    :condition (and
      (over all (at ?train ?loc))
      (at start (at ?pkg ?loc))
    )
    :effect (and
      (at start (not (at ?pkg ?loc)))
      (at end (loaded ?pkg ?train)))
  )

  (:durative-action UNLOAD-TRAIN
    :parameters (?pkg - parcel ?train - train ?loc - station)
    :duration (= ?duration 1)
    :condition (and
      (over all (at ?train ?loc))
      (at start (loaded ?pkg ?train))
    )
    :effect (and
      (at start (not (loaded ?pkg ?train)))
      (at end (at ?pkg ?loc))
    )
  )

  (:durative-action MOVE-AIRPLANE
    :parameters (
       ?plane - plane ?loc-from - airport ?loc-to - airport
    )
    :duration (= ?duration (time-to-fly ?loc-from ?loc-to))
    :condition (and
      (at start (at ?plane ?loc-from))
      (over all (path ?loc-from ?loc-to))
    )
    :effect (and
      (at start (not (at ?plane ?loc-from)))
      (at end (at ?plane ?loc-to))
    )
  )

  (:durative-action LOAD-AIRPLANE
    :parameters (?pkg - parcel ?plane - plane ?loc - airport)
    :duration (= ?duration 1)
    :condition (and
      (over all (at ?plane ?loc))
      (at start (at ?pkg ?loc))
    )
    :effect (and
      (at start (not (at ?pkg ?loc)))
      (at end (loaded ?pkg ?plane)))
  )

  (:durative-action UNLOAD-AIRPLANE
    :parameters (?pkg - parcel ?plane - plane ?loc - airport)
    :duration (= ?duration 1)
    :condition (and
      (over all (at ?plane ?loc))
      (at start (loaded ?pkg ?plane))
    )
    :effect (and
      (at start (not (loaded ?pkg ?plane)))
      (at end (at ?pkg ?loc))
    )
  )

  (:durative-action DRIVE-TRUCK
    :parameters (?truck - truck ?loc-from - location ?loc-to - location ?driver - driver ?city - city)
    :duration (= ?duration (time-to-drive ?loc-from ?loc-to))
    :condition (and
      (at start (at ?truck ?loc-from))
      (at start (located ?loc-from ?city))
      (at start (located ?loc-to ?city))
      (over all (driving ?driver ?truck))
      (over all (path ?loc-from ?loc-to)))
    :effect (and (at start (not (at ?truck ?loc-from)))
      (at end (at ?truck ?loc-to)))
  )

  (:durative-action LOAD-TRUCK
    :parameters (?pkg - parcel ?truck - truck ?loc - location)
    :duration (= ?duration 1)
    :condition (and
      (over all (at ?truck ?loc))
      (at start (at ?pkg ?loc))
      (at start (>= (capacity-weight ?truck) (parcel-weight ?pkg)))
    )
    :effect (and
      (at start (not (at ?pkg ?loc)))
      (at end (loaded ?pkg ?truck))
      (at end (decrease (capacity-weight ?truck) (parcel-weight ?pkg))))
  )

  (:durative-action UNLOAD-TRUCK
    :parameters (?pkg - parcel ?truck - truck ?loc - location)
    :duration (= ?duration 1)
    :condition (and
      (over all (at ?truck ?loc))
      (at start (loaded ?pkg ?truck))
    )
    :effect (and
      (at start (not (loaded ?pkg ?truck)))
      (at end (increase (capacity-weight ?truck) (parcel-weight ?pkg)))
      (at end (at ?pkg ?loc))
    )
  )

  (:durative-action BOARD-TRUCK
    :parameters (?driver - driver ?truck - truck ?loc - location)
    :duration (= ?duration 1)
    :condition (and 
      (over all (at ?truck ?loc))
      (at start (at ?driver ?loc)))
    :effect (and 
      (at start (not (at ?driver ?loc)))
      (at end (driving ?driver ?truck))
    )
  )

  (:durative-action DISEMBARK-TRUCK
    :parameters (?driver - driver ?truck - truck ?loc - location)
    :duration (= ?duration 1)
    :condition (and 
      (over all (at ?truck ?loc))
      (at start (driving ?driver ?truck)))
    :effect (and 
      (at start (not (driving ?driver ?truck)))
      (at end (at ?driver ?loc))
    )
  )
)