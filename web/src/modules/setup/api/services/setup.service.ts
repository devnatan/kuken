import httpService from "@/modules/platform/api/services/http.service"
import type { AxiosResponse } from "axios"
import type { Setup, SetupRequest } from "@/modules/setup/api/models/setup.model.ts"

class SetupService {
    async getSetup(): Promise<Setup> {
        return httpService.get("setup").then((res: AxiosResponse) => res.data as Setup)
    }

    async completeSetup(request: SetupRequest): Promise<Setup> {
        return httpService.post("setup", request).then((res: AxiosResponse) => res.data as Setup)
    }
}

export default new SetupService()
