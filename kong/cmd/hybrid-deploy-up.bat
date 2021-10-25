@ECHO OFF
kubectl apply -f ./kubernetes/camelpd-api-secrets.yaml
kubectl config use-context dev
kubectl create configmap kong-plugin-name-restrict --from-file=kong/plugins/name-restrict
kubectl create secret tls kong-cluster-cert --cert=kong/hybrid/cluster.crt --key=kong/hybrid/cluster.key
helm install cp-release kong/kong --values=kong/hybrid/kong-hybrid-control-plane.yml
helm install dp-release kong/kong --values=kong/hybrid/kong-hybrid-data-plane.yml
helm install dp-release-2 kong/kong --values=kong/hybrid/kong-hybrid-data-plane-2.yml
kubectl get pods