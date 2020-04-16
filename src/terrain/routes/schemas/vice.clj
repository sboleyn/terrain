(ns terrain.routes.schemas.vice
  (:use [common-swagger-api.schema :only [describe]]
        [schema.core :only [defschema Any maybe optional-key]])
  (:import [java.util UUID]))

(defschema BaseListing
  {:name                      (describe String "The name of the resource")
   :namespace                 (describe String "The namespace for the resource")
   :analysisName              (describe String "The name of the analysis the resource is associated with")
   (optional-key :analysisID) (describe (maybe UUID) "The UUID assigned to the analysis")
   :appName                   (describe String "The name of the app the resource is associated with")
   :appID                     (describe UUID "The UUID of the app the resource is associated with")
   :externalID                (describe UUID "The UUID assigned to the job step")
   :userID                    (describe UUID "The UUID assigned to the user that launched the analysis")
   :username                  (describe String "The username of the user that launched the analysis")
   :creationTimestamp         (describe String "The time the resource was created")})

(defschema Deployment
  (merge
    BaseListing
    {:image   (describe String "The container image name used in the K8s Deployment")
     :port    (describe Long "The port number the pods in the deployment are listening on")
     :user    (describe Long "The user ID of the analysis process")
     :group   (describe Long "The group ID of the analysis process")
     :command (describe [String] "The command used to start the analysis")}))

(defschema ContainerStateWaiting
  {:reason  (describe (maybe String) "The reason the container is in the waiting state")
   :message (describe (maybe String) "The message associated with the waiting state")})

(defschema ContainerStateRunning
  {:startedAt (describe String "The time the container started running")})

(defschema ContainerStateTerminated
  {:exitCode    (describe Long "The exit code for the container")
   :signal      (describe Long "The numerical signal sent to the container process")
   :reason      (describe (maybe String) "The reason the container terminated")
   :message     (describe (maybe String) "The message associated with the container termination")
   :startedAt   (describe String "The time the container started")
   :finishedAt  (describe String "The time the container finished")
   :containerID (describe String "The ID of the container")})

(defschema ContainerState
  {(optional-key :waiting)    (describe (maybe ContainerStateWaiting) "The waiting container state")
   (optional-key :running)    (describe (maybe ContainerStateRunning) "The running container state")
   (optional-key :terminated) (describe (maybe ContainerTerminated) "The terminated container state")})

(defschem ContainerStatus
  {:name         (describe String "The name of the container")
   :ready        (describe Boolean "Whether or not the container is ready")
   :restartCount (describe Long "The number of times the container has restarted")
   :state        (describe ContainerState "The current state of the container")
   :lastState    (describe ContainerState "The previous state of the container")
   :image        (describe String "The image name used for the container")
   :imageID      (describe String "The image ID assocaited with the container")
   :containerID  (describe String "The ID associated with the container")
   :started      (describe Boolean "Whether or not the container has started")})

(defschema Pod
  (merge
    BaseListing
    {:phase               (describe String "The pod phase")
     :message             (describe (maybe String) "The message associated with the current state/phase of the pod")
     :reason              (describe (maybe String) "The reason the pod is in the phase")
     :containerStatuses   (describe [ContainerStatus] "The list of container statuses for the pod")
     :initContainerStatus (describe [ContainerStatus] "The list of container status for the init containers in the pod")}))

(defschema ConfigMap
  (merge
    BaseListing
    {:data (describe Any "The data of the config map")}))

(defschema ServicePort
  {:name                         (describe String "The name of the port")
   (optional-key :nodePort)      (describe (maybe Long) "The exposed port on the k8s nodes")
   (optional-key :targetPort)    (describe (maybe Long) "The target port in the selected pods. Will not be present if targetPortName is set")
   (optional-key :targetPortName (describe (maybe String) "The name of the target port on the selected pods. Will not be present if targetPort is set"))
   :port                         (describe Long "The service port")
   :protocol                     (describe String "The protocol the primary service port supports")})

(defschema Service
   (merge
     BaseListing
     {:ports (describe [ServicePort] "The list of ports open in the service")}))