#!/usr/bin/env bash
# set -e
set -uo pipefail
# set -x


SCRIPT_PATH="$(readlink -f "$0")"
SCRIPT_DIR="$(dirname "${SCRIPT_PATH}")"

# "Accept: text/turtle, application/trig, application/rdf+xml;q=0.9, application/ld+json;q=0.9, application/n-triples;q=0.5, application/n-quads;q=0.5"
CURLOPT="-L  --connect-timeout 10 --fail --silent --show-error"
# CURLOPT="-L --fail --silent -w %{stderr}%{http_code}"

main() {
  pushd "${SCRIPT_DIR}/../" || exit 1
  curl_safe "https://old.datahub.io/api/3/action/resource_search?query=format:api/sparql" "./dataset/datahub_sparql_endpoints" "application/json" ".json" "Datahub query for all SPARQL endpoints"
  # pro tip: google for "SPARQL Endpoints Status tool monitors the availability, performance, interoperability and discoverability"
  curl_safe "https://sparqles.demo.openlinksw.com/api/endpoint/list" "./dataset/sparqles_endpoint_list-openlink_3dfed" "application/json" ".json" "Endpoint list from OpenlinkSW SPARQLES instance for 3DFed"
  curl_safe "https://sparqles.ai.wu.ac.at/api/endpoint/list" "./dataset/sparqles_endpoint_list-tuwien" "application/json" ".json" "Endpoint list from the TU Wien SPARQLES instance"
  curl_safe "https://sparqles.okfn.org/api/endpoint/list" "./dataset/sparqles_endpoint_list-okfn" "application/json" ".json" "Endpoint list from the OKFN SPARQLES instance"
  curl_safe "https://sparqles.org/api/endpoint/list" "./dataset/sparqles_endpoint_list-okfn" "application/json" ".json" "Endpoint list from SPARQLES rehosted  .org instance"
  popd || exit 1
}

function delete_if_html() {
   file_path="$1"

   mime_type=$(file --mime-type "$file_path" | awk '{print $2}')

   # Check if the MIME type is text/html
   if [ "$mime_type" = "text/html" ] || [ "$mime_type" = "text/xml" ]; then
      # Additional check for common HTML tags in the first 5 lines
      if head -n 5 "$file_path" | grep -q "<html" || head -n 5 "$file_path" | grep -q "<!DOCTYPE html"; then
         # Delete the file
         rm "$file_path"
         echo -n " (deleted - HTML detected)"
         # echo "File $file_path has been deleted because its MIME type is text/html or text/xml and it contains HTML tags in the first 5 lines."
      # else
         # echo
         # echo "File $file_path has a MIME type of text/html or text/xml but does not contain common HTML tags in the first 5 lines. It will not be deleted."
      fi
   fi
}

function curl_safe() {
   uri="$1"
   outpath="$2"
   accept="$3"
   ext="$4"
   name="$5"

   echo -e -n "\t${name}\t"
   # use a temp file to avoid deleting ontologies when endpoints are down
   # and to avoid clobbering
   # TODO: consider clobbering on 404 to make it visible in the history - bad UX
   { curl "$uri" --header "Accept: ${accept}" $CURLOPT >"${outpath}${ext}.tmp" ; } || { rm -f "${outpath}${ext}.tmp"; }
   delete_if_html "${outpath}${ext}.tmp"
   if [ -s "${outpath}${ext}.tmp" ]; then
      if [[ "${ext}" == ".json" ]] && [[ -x "$(command -v jq >/dev/null 2>&1)" ]]; then
        echo "jq postprocess"
        jq '.' "${outpath}${ext}.tmp" > "${outpath}${ext}"
      else
        mv "${outpath}${ext}.tmp" "${outpath}${ext}"
      fi
      echo -n " ✅";
   else
      rm -f "${outpath}${ext}.tmp"
   fi
   echo
}

main "$@"; exit
