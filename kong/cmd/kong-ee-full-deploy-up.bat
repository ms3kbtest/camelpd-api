@ECHO OFF
kubectl apply -f ./kubernetes/camelpd-api-secrets.yaml
kubectl config use-context dev
kubectl create configmap kong-plugin-name-restrict --from-file=kong/plugins/name-restrict
kubectl create configmap kong-plugin-custom-json-request-validation --from-file=kong/plugins/custom-json-request-validation
kubectl create secret generic kong-session-config --from-file=kong/ee/admin_gui_session_conf --from-file=kong/ee/portal_session_conf
kubectl create secret generic kong-enterprise-superuser-password --from-literal=password=kong
kubectl create secret generic kong-enterprise-license --from-file=license=kong/ee/license.json
helm install kong-ee kong/kong --values=kong/ee/kong-enterprise-full-k4k8s.yml -n kong